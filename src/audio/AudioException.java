package audio;


public class AudioException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AudioException(String message) {
		super(message);
	}

	public AudioException(String message, Exception e) {
		super(message, e);
	}

}
