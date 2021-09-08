package com.lsz.seckill.util;

import java.util.UUID;

public class UUIDUtil {
	public static String uuid() {

		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
