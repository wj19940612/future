package com.jnhyxx.html5.netty;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jnhyxx.html5.domain.market.FullMarketData;
import com.jnhyxx.html5.domain.market.ServerIpPort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private static final String TAG = "NettyClient";

    private EventLoopGroup mWorkerGroup;
    private Bootstrap mBootstrap;
    private Channel mChannel;

    private String mHost;
    private Integer mPort;
    private boolean mClosed;

    private MarketConn mMarketConn;
    private ChattingConn mChattingConn;

    private NettyClientHandler.Callback mCallback;
    private List<NettyHandler> mHandlerList;
    private QuotaDataFilter mQuotaDataFilter;

    private static NettyClient mInstance;

    public static NettyClient getInstance() {
        if (mInstance == null) {
            mInstance = new NettyClient();
        }
        return mInstance;
    }

    public NettyClient() {
        this.mHandlerList = new ArrayList<>();
        this.mCallback = new NettyClientHandler.Callback() {
            @Override
            public void onChannelActive(ChannelHandlerContext ctx) {
                Log.d(TAG, "onChannelActive: ");
            }

            @Override
            public void onChannelInActive(ChannelHandlerContext ctx) {
                Log.d(TAG, "onChannelInActive: ");
                ctx.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        connect();
                    }
                }, 1, TimeUnit.MILLISECONDS);
            }

            @Override
            public void onReceiveData(String data) {
                //Log.d(TAG, "onReceiveData: " + data);
                processOriginalData(data);
            }

            @Override
            public void onError(ChannelHandlerContext ctx, Throwable cause) {
                Log.d(TAG, "onError: ");
            }
        };
    }

    private void processOriginalData(String data) {
        if (mMarketConn != null) {
            try {
                FullMarketData marketData = new Gson().fromJson(data, FullMarketData.class);
                if (mQuotaDataFilter != null && mQuotaDataFilter.filter(marketData)) return;
            } catch (JsonSyntaxException e) {
                onError(e.getMessage());
                return;
            }
        }

        for (int i = 0; i < mHandlerList.size(); i++) {
            Handler handler = mHandlerList.get(i);
            Message message = handler.obtainMessage(NettyHandler.WHAT_DATA, data);
            handler.sendMessage(message);
        }
    }

    private void onError(String msg) {
        for (int i = 0; i < mHandlerList.size(); i++) {
            Handler handler = mHandlerList.get(i);
            Message message = handler.obtainMessage(NettyHandler.WHAT_ERROR, msg);
            handler.sendMessage(message);
        }
    }

    public void addNettyHandler(NettyHandler handler) {
        if (mHandlerList != null) {
            mHandlerList.add(handler);
        }
    }

    public void removeNettyHandler(NettyHandler handler) {
        if (mHandlerList != null) {
            mHandlerList.remove(handler);
        }
    }

    public void setChattingIpAndPort(String host, String port) {
        try {
            this.mHost = host;
            this.mPort = Integer.valueOf(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mPort = null;
        }
    }

    public void start(int teacherId, String tokenStr) {
        mClosed = false;
        mChattingConn = new ChattingConn(teacherId, tokenStr);

        mWorkerGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap()
                .group(mWorkerGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyInitializer(mCallback));
        connect();
    }

    public void start(String contractCode) {
        mClosed = false;
        mMarketConn = new MarketConn(contractCode);
        mQuotaDataFilter = new DefaultQuotaDataFilter(contractCode);

        mWorkerGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap()
                .group(mWorkerGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyInitializer(mCallback));

        ServerIpPort ipPort = ServerIpPort.getMarketServerIpPort();
        if (ipPort != null && ipPort.isValid()) {
            mHost = ipPort.getIp();
            mPort = Integer.valueOf(ipPort.getPort());
            connect();
        } else {
            ServerIpPort.requestMarketServerIpAndPort(new ServerIpPort.Callback() {
                @Override
                public void onSuccess(ServerIpPort ipPort) {
                    Log.d(TAG, "requestMarketServerIpAndPort success: " + ipPort);
                    mHost = ipPort.getIp();
                    mPort = Integer.valueOf(ipPort.getPort());
                    connect();
                }
            });
        }
    }

    private void connect() {
        if (mClosed && mBootstrap != null) return;

        ChannelFuture channelFuture = mBootstrap.connect(mHost, mPort);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    mChannel = channelFuture.sync().channel();

                    if (mMarketConn != null) {
                        mChannel.writeAndFlush(mMarketConn.toJson());
                    }

                    if (mChattingConn != null) {
                        Log.d(TAG, "operationComplete: " + mChattingConn.toJson());
                        mChannel.writeAndFlush(mChattingConn.toJson());
                    }
                } else {
                    Throwable throwable = channelFuture.cause();
                    throwable.printStackTrace();
                    onError(throwable.getMessage());
                }
            }
        });
    }

    public void sendMessage(String msg) {
        if (!mClosed && mChattingConn != null && mChannel != null) {
            mChattingConn.setMsg(msg);
            mChannel.writeAndFlush(mChattingConn.toJson());
        }
    }

    public void stop() {
        mClosed = true;
        mMarketConn = null;
        mChattingConn = null;

        if (mWorkerGroup != null) {
            mWorkerGroup.shutdownGracefully();
        }
        if (mChannel != null) {
            ChannelFuture future = mChannel.close();
            Log.d(TAG, "stop: " + future.toString());
        }

    }

    private static class ChattingConn {
        private int id;
        private String token1;
        private String token2;
        private String msg;

        public ChattingConn(int teacherId, String originalTokenStr) {
            this.id = teacherId;
            initTokens(originalTokenStr);
        }

        private void initTokens(String originalTokenStr) {
            if (!TextUtils.isEmpty(originalTokenStr)) {
                String[] splits = originalTokenStr.split(";");
                for (int i = 0; i < splits.length; i++) {
                    String split = splits[i].trim();
                    splits[i] = split.substring(split.indexOf("\""));
                }
                if (splits.length >= 2) {
                    token1 = splits[0];
                    token2 = splits[1];
                }
            }
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String toJson() {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            return gson.toJson(this);
        }
    }

    private static class MarketConn {
        private String code;

        public MarketConn(String code) {
            this.code = code;
        }

        public String toJson() {
            return new Gson().toJson(this);
        }
    }

    public interface QuotaDataFilter {
        /**
         * Filter quota data
         *
         * @param data
         * @return if the data need to be filtered return true, false otherwise
         */
        boolean filter(FullMarketData data);
    }

    private static class DefaultQuotaDataFilter implements QuotaDataFilter {

        private String mContractCode;

        public DefaultQuotaDataFilter(String contractCode) {
            mContractCode = contractCode;
        }

        @Override
        public boolean filter(FullMarketData data) {
            return !data.getInstrumentId().equalsIgnoreCase(mContractCode);
        }
    }
}