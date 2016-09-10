/*
 * Copyright (c) 2015 www.caniu.com - 版权所有
 * 
 * This software is the confidential and proprietary information of
 * luckin Group. You shall not disclose such confidential information 
 * and shall use it only in accordance with the terms of the license 
 * agreement you entered into with www.cainiu.com
 */
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
