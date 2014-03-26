package audio;

import org.lwjgl.openal.AL10;

public class ALException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ALException(int errorCode) {
		super(getMessage(errorCode));
	}

	public ALException(int errorCode, Exception e) {
		super(getMessage(errorCode), e);
	}

	private static String getMessage(int errorCode) {
		return "Audio error: " + errorCode + ": " + getALErrorString(errorCode);
	}

	private static String getALErrorString(int errorCode) {
		switch (errorCode) {
		case AL10.AL_NO_ERROR:
			return "AL_NO_ERROR";
		case AL10.AL_INVALID_NAME:
			return "AL_INVALID_NAME";
		case AL10.AL_INVALID_ENUM:
			return "AL_INVALID_ENUM";
		case AL10.AL_INVALID_VALUE:
			return "AL_INVALID_VALUE";
		case AL10.AL_INVALID_OPERATION:
			return "AL_INVALID_OPERATION";
		case AL10.AL_OUT_OF_MEMORY:
			return "AL_OUT_OF_MEMORY";
		default:
			return "No such error code";
		}
	}
}
