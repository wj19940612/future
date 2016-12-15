package com.jnhyxx.html5.netty;

import android.text.TextUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SocketEncoder extends MessageToByteEncoder<String> {

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		if(TextUtils.isEmpty(msg)) {
			return ;
		}
		byte[] message = msg.getBytes("GBK");
		out.writeInt(message.length);
		out.writeBytes(message);
	}
}
