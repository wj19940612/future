package com.jnhyxx.html5.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class SocketDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while (true) {
			if (in.readableBytes() <= 4) {
				break;
			}
			in.markReaderIndex();
			int length = in.readInt();
			if(length <= 0) {
				throw new Exception("a negative length occurd while decode!");
			}
			if (in.readableBytes() < length) {
				in.resetReaderIndex();
				break;
			}
			byte[] msg = new byte[length];
			in.readBytes(msg);
			out.add(new String(msg, "GBK"));
		}
	}
}
