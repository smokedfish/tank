/*
 * MainActivity.java in package com.SeewaldSolutions.ToyCollect
 * (C) 2014 Dr. Alexander K. Seewald, Seewald Solutions
 * Authors: Georg Weissinger, Alexander K. Seewald
 * License: GPLv3  http://www.gnu.org/licenses/gpl-3.0.txt
 */

/* Formatting: tabstop=2 */

package com.SeewaldSolutions.ToyCollect;

import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.io.FileWriter;
import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;

public class MainActivity extends Activity {

	public final int DIALOG_HOST = 1;
	
	public int radius;
	public int breite=-1;
	public int hohe=-1;
	public int kreisx;
	public int kreisy;
	
  public SurfaceView mV;
  public MyImageView iV;

	public String host;
	public EditText hostEdit;
   
  ControlThread cot;
  CommunicationThread ct;
	
  protected void onPause() {
  	super.onPause();
   	System.exit(0);
  }
    
    
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
     
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    mV = (SurfaceView)this.findViewById(R.id.surface);

		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		this.host = sp.getString("h","192.168.42.42");

		this.hostEdit = new EditText(this);

    ControlThread cot = new ControlThread(this,this.host);
    mV.setOnTouchListener(cot);
    Thread to = new Thread(cot);
    to.start();
       
    CommunicationThread ct = new CommunicationThread(this,this.host);
    Thread t = new Thread(ct);
    t.start();
  }
    
  class ControlThread implements Runnable, OnTouchListener {
		Socket socket = null;
		OutputStream out = null;
		byte[] message = new byte[2];
		String host = null;
		final int port = 5002;
		int led=0;
		MainActivity m;
		
		int g, d;
		boolean send;
		boolean communication;
		
    public ControlThread(MainActivity m, String host) {
      this.m = m; this.send=false; this.host = host;
      this.communication = false;
    }
        
    public void initSocket() {
	    try {
				socket = new Socket(host, port);
				if (socket!=null) {	out = socket.getOutputStream(); }
				this.communication = true;
				
				sendMsg(-128,-128);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				socket=null;
				out=null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				socket=null;
				out=null;
			}
    }

		public void run() {
			int cnt=0;
			
			for (;;) {
				if (!this.communication && cnt % 100 == 0) {
					initSocket();
				}
				if (this.send) {
					this.sendMsg(this.g,this.d);
					this.send=false; this.g=this.d=0;
				}
				try {
					Thread.sleep(10);		
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cnt++;
			}
		}
		
		protected void sendMsg_(int g, int d) {
			this.g=g; this.d=d;
			this.send=true;
		}

		private void sendMsg(int g, int d) {
			if (socket==null || out==null) {
				initSocket();
				
				if (socket==null || out==null) { return; }
			}
			
	    message[0] = (byte)g;
	    message[1] = (byte)d;
	  	try {
				out.write(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				socket=null; out=null;
			}
	   	this.send=false;
	  }
		
	  @SuppressLint("ClickableViewAccessibility") public boolean onTouch(View v, MotionEvent me) {
	   	if (breite<=0 || hohe<=0) { return true; }
	    	
	    float x = me.getX();
	    float y = me.getY();
	        
	    float x1,y1;
	    int geschwindigkeit=0;
	    float vg;
	    int winkel=0;			//message[0]
	        
	    boolean chg=false;
	        
	    switch (me.getAction()) {
	        
	      case MotionEvent.ACTION_DOWN:
	      case MotionEvent.ACTION_MOVE:

					if (y>=hohe-radius/3 && x>=breite-2*breite/3 && x<=breite-breite/3) {
						hostEdit.setText(this.host);
						showDialog(DIALOG_HOST);
						break;
					}

	        	
	       	if (y>=25*radius/100 && y<=50*radius/100) {
	       		if(x<=30*radius/100) {
	       			led=0;
	       		} else if (x>=breite-30*radius/100) {
	       			led=126;
	       		} else {
	       			led= (int) (126*x/(breite-30*radius/100));
	       		}
	        		
	          geschwindigkeit = -128; winkel = led;
	          chg=true;
	          break;
	        }

		      x1 = x-kreisx;
		      y1 = y-kreisy;

	       	vg = (float) sqrt(y1*y1+x1*x1);
	        	
	       	if (vg>radius*1.25f) { break; }
	        
	     		if (x1>=0 &&y1>=0) {
	        	if (vg >=radius)	 vg= radius;
	        	if (vg <= -radius)	 vg =-radius;
	        	geschwindigkeit = Math.round((vg/radius)*-127); 
	        }
	        
	     		if (x1<0 && y1<0) {
	    	 		x1= x1*-1;
	    	 		y1 = y1*-1;
	        	if (vg >=radius)	 vg= radius;
	        	if (vg <= -radius)	 vg =-radius;
	    	 		geschwindigkeit = Math.round((vg/radius)*127); 
	        }
	     
	     		if (x1<0 && y1>=0) {
	    	 		x1= x1*-1;
	        	if (vg >=radius)	 vg= radius;
	        	if (vg <= -radius)	 vg =-radius;
	    	 		geschwindigkeit = Math.round((vg/radius)*-127); 
	        }
	     
	     		if (x1>=0 && y1<0) {
	    			y1 = y1*-1;
	        	if (vg >=radius)	 vg= radius;
	        	if (vg <= -radius)	 vg =-radius;
	    	 		geschwindigkeit = Math.round((vg/radius)*127); 
	        }
	        
	       	//WINKEL
	       
	     		float rad, grad;
	     		rad =  (float) atan2(y1,x1);
	     		grad = (float) ((180/Math.PI)*rad);
	     
	     		float winkel_ = grad/90;
	     
	     		if (x>(breite/2)) {
	    	 		if (grad<18.0f) {
	    		 		winkel=+127;
	    		 		geschwindigkeit=Math.abs(geschwindigkeit);
	    	 		} else {
	    		 		winkel = Math.round(((90.0f-grad)/72.0f)*126);
	    	 		}	    	 
	     		}

	     		if (x<=(breite/2)) {
	    	 		if (grad<18.0f) {
	    		 		winkel=-127;
	    		 		geschwindigkeit=Math.abs(geschwindigkeit);
	    	 		} else {
	    		 		winkel = Math.round(((90.0f-grad)/72.0f)*-126);
	    	 		}	    	 
	     		}
	     		winkel = -winkel;
	     		chg=true;
	     		break;
	     
	   		case MotionEvent.ACTION_UP:
		   		winkel=0; geschwindigkeit=0;
	     		chg=true; 
	     		break;
	   	}
	     
	    if (chg) { sendMsg_(geschwindigkeit,winkel); }
	        
	    //System.out.println("X:" +x +"     Y:"+y);
	    //System.out.println("Geschwindigkeit:" +geschwindigkeit+"    Winkel: "+winkel);
	        
	    return true;
	  } 		
	}
    
  class CommunicationThread implements Runnable {	 
    public FileOutputStream out;
    private Socket clientSocket;
    private InputStream input;
    private MainActivity m;
    private String hex = "0123456789ABCDEF";
		private String host = null;

    public CommunicationThread(MainActivity m, String host) {
      this.m = m; this.host = host;
    }

		@Override
		public void run() {
			out=null;
					
			this.clientSocket = null;
			while (this.clientSocket == null) {
				try {
    			this.clientSocket = new Socket(this.host , 5001);
    		} catch (UnknownHostException e) {
    			e.printStackTrace();
	    	  this.clientSocket=null;
	    	} catch (IOException e) {
	    	  e.printStackTrace();
	    	  this.clientSocket=null;
	    	}
						
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
					
      try {
        this.input = new BufferedInputStream(this.clientSocket.getInputStream(),262144);
      } catch (IOException e) {
        e.printStackTrace();
      }

      int b = 0;
      int [] block = new int[4];
	    	        
      try {
      	block[0] = this.input.read();
       	block[1] = this.input.read();
       	block[2] = this.input.read();
       	block[3] = this.input.read();
			} catch (IOException e) {
				return;
			}
	    	        
      // init media decoder
      MediaCodec codec = MediaCodec.createDecoderByType("video/avc");
      while (m.mV.getHolder().isCreating()) { try{Thread.sleep(100); } catch (Exception e) {}; }
      try {
				Thread.sleep(250);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

      codec.configure(MediaFormat.createVideoFormat("video/avc", 720, 1280),m.mV.getHolder().getSurface(),null,0);
      codec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
      codec.start();
      ByteBuffer[] inputBuffers = codec.getInputBuffers();	    	        

      long tStart = SystemClock.elapsedRealtime();
	    	        
      // 512k block size ~ est. max. length NAL
      byte [] nal = new byte[512*1024];
      nal[0] = (byte)block[0];
      nal[1] = (byte)block[1];
      nal[2] = (byte)block[2];
      nal[3] = (byte)block[3];
      int nalPos = 4;
      int pos=4;
      int blockStart=0;
      int inputBufferIndex;
			int cnta=0;

      while (block[3]!=-1) {
       	if (block[0] == 0x00 && block[1] == 0x00 && block[2] == 0x01) {
      		if (blockStart!=-1) {
       			nalPos-=5;
      			if (nalPos<=2) {
							blockStart = pos;
		    	    nal[0] = (byte)block[0];
		    	    nal[1] = (byte)block[1];
		    	    nal[2] = (byte)block[2];
		    	    nal[3] = (byte)block[3];
		    	    nalPos=4;
		    	        		
			    	  block[0] = block[1];
			    	  block[1] = block[2];
			    	  block[2] = block[3];
			    	        	
			    	  try {
			    	  	block[3] = this.input.read();
			    	   	nal[nalPos++] = (byte)block[3];
			    	  } catch (IOException e) {
			    	   	block[3] = -1;
			    	  }
			    	  pos++;
							continue;
						}
	    	        			
		   	    inputBufferIndex = codec.dequeueInputBuffer(-1);
	    			    	    
	    		  if (inputBufferIndex >= 0) {
		   	      // fill inputBuffers[inputBufferIndex] with valid data
		   	      inputBuffers[inputBufferIndex].position(1);
		   	      inputBuffers[inputBufferIndex].put(nal, 0, nalPos);

		   	      inputBuffers[inputBufferIndex].position(0);
		   	      inputBuffers[inputBufferIndex].put((byte) 0);
		   	      inputBuffers[inputBufferIndex].position(0);

		   	      codec.queueInputBuffer(inputBufferIndex,0,nalPos+1,(SystemClock.elapsedRealtime()-tStart)*1000,(nal[3]==0x27 || nal[3]==0x28) ? MediaCodec.BUFFER_FLAG_CODEC_CONFIG : 0);
	    		  }
       		}

       		blockStart = pos;
	    	        		
       		nal[0] = (byte)block[0];
       		nal[1] = (byte)block[1];
       		nal[2] = (byte)block[2];
       		nal[3] = (byte)block[3];
       		nalPos=4;
       	}
	    	        	
       	block[0] = block[1];
       	block[1] = block[2];
       	block[2] = block[3];
	    	        	
       	if (cnta % 1000 == 999) {
       		MediaCodec.BufferInfo bi = new MediaCodec.BufferInfo();
       		int outputBufferIndex = -1;
       		try {
       			outputBufferIndex = codec.dequeueOutputBuffer(bi, 0/*25000*/);
       		} catch (IllegalStateException ise) {
       			codec.release();
       			return;
       		}
       		if (outputBufferIndex>=0) {
       			codec.releaseOutputBuffer(outputBufferIndex, true);
       		}
       	}
			    	    
	   	  cnta++;
	    	        	
       	try {
       		block[3] = this.input.read();
       		nal[nalPos++] = (byte)block[3];
       	} catch (IOException e) {
       		block[3] = -1;
       	}
       	pos++;
      }
		}	
  }

	protected Dialog onCreateDialog(int id) {
		Dialog dialog; AlertDialog.Builder builder;

		switch (id) {
			case DIALOG_HOST:
				this.hostEdit.setText(this.host);
				builder = new AlertDialog.Builder(this);
				builder.setTitle("TCclient IP-Address (static)");
				builder.setView(this.hostEdit);
				builder.setCancelable(true)
				.setPositiveButton("Ok (needs restart)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					SharedPreferences sp = getPreferences(MODE_PRIVATE);
					SharedPreferences.Editor se = sp.edit();
					se.putString("h",hostEdit.getText().toString());
					se.commit();
					finish();
					startActivity(getIntent());
				} } )
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				} } );

				dialog = builder.create();
				break;
			default:
				dialog = null;
				break;
		}

		return dialog;
	}
}
