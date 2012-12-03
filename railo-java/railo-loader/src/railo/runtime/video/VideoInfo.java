package railo.runtime.video;

import railo.runtime.type.Struct;

public interface VideoInfo {
	
	public long getAudioBitrate();
	/**
	 * @return the audioChannels
	 */
	public String getAudioChannels();

	/**
	 * @return the audioCodec
	 */
	public String getAudioCodec();

	/**
	 * @return the audioSampleRate
	 */
	public long getAudioSamplerate();

	/**
	 * @return the duration
	 */
	public long getDuration();
	
	/**
	 * @return the bitrate
	 */
	public long getVideoBitrate();

	/**
	 * @return the framerate
	 */
	public double getFramerate();

	/**
	 * @return the videoCodec
	 */
	public String getVideoCodec();
	
	/**
	 * @return the videoFormat
	 */
	public String getVideoFormat();

	/**
	 * @return the height
	 */
	public int getHeight();

	/**
	 * @return the width
	 */
	public int getWidth();
	
	/**
	 * returns the information as Struct
	 * @return
	 */
	public Struct toStruct();
}
