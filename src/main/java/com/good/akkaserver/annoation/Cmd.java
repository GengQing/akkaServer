package com.good.akkaserver.annoation;

import java.lang.annotation.*;

/**
 * 消息处理方法标记
 * ericSong
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cmd {
	int value();
}
