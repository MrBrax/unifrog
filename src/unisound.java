import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class unisound implements Runnable {
	
	static AdvancedPlayer mp3_player = null;
    static BufferedInputStream mp3_in = null;
    private int pausedOnFrame = 0;
    
    private boolean looped = false;
    private boolean stopped = true;
    private String file = "";
    private Thread thread;
    
    private PlaybackListener pb = new PlaybackListener() {
    	
    	 @Override
	    public void playbackFinished(PlaybackEvent event) {
	        
	    	thread.interrupt();
	    	
	        if( looped ){ 
	        	play( file, looped );
	        }else{
	        	stopped = true;
	        }

	    }
    	 
    };
    
    public static void main() {
    	
    }
    
    public void play( String sFile, boolean bLoop ) {
    	
    	file = sFile;
    	looped = bLoop;

		try {
			
			URL url = unifrog.class.getResource( file );
			
			mp3_in = new BufferedInputStream( url.openStream() );
			mp3_player = new AdvancedPlayer(mp3_in);
			
			mp3_player.setPlayBackListener(pb);
			
			thread = new Thread( new unisound() );
			thread.start();
			
			stopped = false;
			
			//(new Thread( new unisound())).start();
		  
		} catch (MalformedURLException ex) {
		} catch (IOException e) {
		} catch (JavaLayerException e) {
		} catch (NullPointerException ex) {
		}
			
	}
    
    public void isplaying(){
    	
    }
    
    public void stop() {
    	if(stopped) return;
    	System.out.println("Stop music playback");
    	looped = false;
    	mp3_player.stop();
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			mp3_player.play();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
