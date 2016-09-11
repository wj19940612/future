package com.jnhyxx.html5.netty;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jnhyxx.html5.domain.market.FullMarketData;

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

    private final static String DEFAULT_HOST = "139.196.178.195";
    private final static int DEFAULT_PORT = 13502;

    private EventLoopGroup mWorkerGroup;
    private Bootstrap mBootstrap;

    private String mHost;
    private Integer mPort;
    private Integer mId;
    private boolean mClosed;

    private NettyClientHandler.Callback mCallback;
    private QuotaDataFilter mQuotaDataFilter;

    private List<NettyHandler> mHandlerList;

    private static NettyClient mInstance;

    public interface QuotaDataFilter {
        /**
         * Filter quota data
         * @param data
         * @return if the data need to be filtered return true, false otherwise
         */
        boolean filter(FullMarketData data);
    }

    public static NettyClient getInstance() {
        if (mInstance == null) {
            mInstance = new NettyClient();
        }
        return mInstance;
    }

    private NettyClient() {
        this.mHandlerList = new ArrayList<>();
        this.mCallback = new NettyClientHandler.Callback() {
            @Override
            public void onChannelActive(ChannelHandlerContext ctx) {
                Log.d("TEST", "onChannelActive: ");
            }

            @Override
            public void onChannelInActive(ChannelHandlerContext ctx) {
                Log.d("TEST", "onChannelInActive: ");
                ctx.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        connect();
                    }
                }, 1, TimeUnit.MILLISECONDS);
            }

            @Override
            public void onReceiveData(String data) {
                if (data.indexOf("msgType") > -1) {
                    //handleRealTimeStrategy(data);
                } else {
                    handleRealTimeQuotaData(data);
                }
            }

            @Override
            public void onError(ChannelHandlerContext ctx, Throwable cause) {

            }
        };
    }

    private void handleRealTimeQuotaData(String data) {
        try {
            FullMarketData marketData = new Gson().fromJson(data, FullMarketData.class);

            if (mQuotaDataFilter != null && mQuotaDataFilter.filter(marketData)) {
                return;
            }
            if (mId != null) {
                onReceiveSingleData(marketData);
            }
        } catch (JsonSyntaxException e) {
            onError(e.getMessage());
        }
    }

    private void onReceiveSingleData(FullMarketData data) {
        for (int i = 0; i < mHandlerList.size(); i++) {
            Handler handler = mHandlerList.get(i);
            Message message = handler.obtainMessage(NettyHandler.WHAT_SINGLE_DATA, data);
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

    public void setHostAndPort(String host, String port) {
        try {
            this.mHost = host;
            this.mPort = Integer.valueOf(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mPort = null;
        }
    }

    public void setId(Integer id) {
        mId = id;
    }

    public void setQuotaDataFilter(QuotaDataFilter quotaDataFilter) {
        mQuotaDataFilter = quotaDataFilter;
    }

    public void start() {
        mClosed = false;

        mWorkerGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap()
                .group(mWorkerGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyInitializer(mCallback));
        connect();
    }

    private void connect() {
        if (mClosed && mBootstrap != null) return;
        Log.d("TEST", "connect: ");
        if (mHost == null || mPort == null) {
            mHost = DEFAULT_HOST;
            mPort = DEFAULT_PORT;
        }

        ChannelFuture channelFuture = mBootstrap.connect(mHost, mPort);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    Channel channel = channelFuture.sync().channel();
                    if (mId != null) {
                        channel.writeAndFlush(NettyLoginFactory2.createRegisterJson(mId).toString());
                    }
                } else {
                    Throwable throwable = channelFuture.cause();
                    throwable.printStackTrace();
                    onError(throwable.getMessage());
                }
            }
        });
    }

    public void stop() {
        mClosed = true;
        if (mWorkerGroup != null) {
            mWorkerGroup.shutdownGracefully();
        }
    }
}
