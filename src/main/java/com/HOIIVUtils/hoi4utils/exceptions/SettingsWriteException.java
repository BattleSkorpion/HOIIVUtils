package com.HOIIVUtils.hoi4utils.exceptions;

import java.io.IOException;

public class SettingsWriteException extends RuntimeException {
	public SettingsWriteException(String message, IOException e) {
		super(message, e);
	}
}
