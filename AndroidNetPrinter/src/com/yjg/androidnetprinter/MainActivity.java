package com.yjg.androidnetprinter;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * ���ִ�ӡ���͵�ʾ��
 * @author yjg
 * @version 1.0
 */
public class MainActivity extends Activity
{
	//����
	private static final String	TAG	= MainActivity.class.getSimpleName();
	
	static final String HELLO_WORLD = "��ӡ�Ĳ������� + hello world +!@#$%^&*()_";
	
	
	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //�����Ҫ��鲢��װsend2printer��
        // check for and install Send 2 Printer if needed
             
        if( PrintUtils.isSend2PrinterInstalled(this) == false ){
        	PrintUtils.launchMarketPageForSend2Printer( this );
        	return;
        }
        
        //����GUI��ť
        //Button��Ƭ
        Button btnTestCanvas = (Button)findViewById( R.id.btnTestCanvas );
        btnTestCanvas.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printCanvasExample();
			}
        });
        //Buttonλͼ
        Button btnTestCanvasAsBitmap = (Button)findViewById( R.id.btnTestCanvasAsBitmap );
        btnTestCanvasAsBitmap.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printCanvasAsBitmapExample();
			}
        });
        //Button����
        Button btnTestText = (Button)findViewById( R.id.btnTestText );
        btnTestText.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printTextExample();
			}
        });
        //BUtton��ҳ
        Button btnTestHtml = (Button)findViewById( R.id.btnTestHtml );
        btnTestHtml.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printHtmlExample();
			}
        });
        //Button��ַ
        Button btnTestHtmlUrl = (Button)findViewById( R.id.btnTestHtmlUrl );
        btnTestHtmlUrl.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printHtmlUrlExample();
			}
        });
        //button�ı��ļ�
        Button btnTestTextFile = (Button)findViewById( R.id.btnTestTextFile );
        btnTestTextFile.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printTextFileExample();
			}
        });
        //Button��ҳ�ļ�
        Button btnTestHtmlFile = (Button)findViewById( R.id.btnTestHtmlFile );
        btnTestHtmlFile.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printHtmlFileExample();
			}
        });
    }
    
 
    /**
     * Ϊ��ӡ��������Ƭ��ͼ
     * NOTE: Android 1.5 does not properly support drawBitmap() serialize/deserialize across process boundaries.
     * NOTE��1.5�汾����ȫ֧��drawBitmap()���ع��ֽ��߹���
     * �������Ҫ��ʾbitmap����������������{@link #printCanvasAsBitmapExample()}
     */
    void printCanvasExample(){
    	// ��������������Ⱦ
    	Picture picture = new Picture();
    	Canvas c = picture.beginRecording( 240, 240 );
    	// �ð�ɫ��䱳��
    	c.drawRGB( 0xFF, 0xFF, 0xFF );
    	// �������
    	Paint p = new Paint();
    	Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        p.setTextSize( 18 );
        p.setTypeface( font );
        p.setAntiAlias(true);     	
    	Rect textBounds = new Rect();
    	p.getTextBounds( HELLO_WORLD, 0, HELLO_WORLD.length(), textBounds );
    	int x = (c.getWidth() - (textBounds.right-textBounds.left)) / 2;
    	int y = (c.getHeight() - (textBounds.bottom-textBounds.top)) / 2;
    	c.drawText( HELLO_WORLD, x, y, p );
    	
    	// ��Ⱦͼ��
    	Bitmap icon = BitmapFactory.decodeResource( getResources(), R.drawable.icon );
    	c.drawBitmap( icon, 0, 0, null );

    	// ֹͣ��Ⱦ
    	picture.endRecording();
    	
    	// �����ӡ����
    	File f = PrintUtils.saveCanvasPictureToTempFile( picture );
    	if( f != null ){
    		PrintUtils.queuePictureStreamForPrinting( this, f );
    	}
    }
    
    
    /**
     * ����һ��bitmap Ȼ���� bitmap ���ڴ�ӡ.
     */
    void printCanvasAsBitmapExample(){
    	// ��������������Ⱦ
    	Bitmap b = Bitmap.createBitmap( 240, 240, Bitmap.Config.RGB_565 );
    	Canvas c = new Canvas( b );
    	// �ð�ɫ��䱳��
    	c.drawRGB( 0xFF, 0xFF, 0xFF );
    	// �������
    	Paint p = new Paint();
    	Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        p.setTextSize( 18 );
        p.setTypeface( font );
        p.setAntiAlias(true);     	
    	Rect textBounds = new Rect();
    	p.getTextBounds( HELLO_WORLD, 0, HELLO_WORLD.length(), textBounds );
    	int x = (c.getWidth() - (textBounds.right-textBounds.left)) / 2;
    	int y = (c.getHeight() - (textBounds.bottom-textBounds.top)) / 2;
    	c.drawText( HELLO_WORLD, x, y, p );
    	
    	// ��Ⱦͼ��
    	Bitmap icon = BitmapFactory.decodeResource( getResources(), R.drawable.icon );
    	c.drawBitmap( icon, 0, 0, null );

    	// ������Ⱦ����
    	try{
	    	File f = PrintUtils.saveBitmapToTempFile( b, Bitmap.CompressFormat.PNG );
	    	if( f != null ){
	    		PrintUtils.queueBitmapForPrinting( this, f, Bitmap.CompressFormat.PNG );
	    	}
    	}
    	catch( Exception e ){
    		Log.e( TAG, "failed to save/queue bitmap", e );
    	}
    }
    
    
    /**
     * ���ʹ�ӡ������
     */
    void printTextExample(){
		PrintUtils.queueTextForPrinting( this, HELLO_WORLD );
    }
    
    
    /**
     *  ����html������ӡ
     */
    void printHtmlExample(){
		StringBuilder buf = new StringBuilder();
		buf.append( "<html>" );
		buf.append( "<body>" );
		buf.append( "<h1>" ).append( HELLO_WORLD ).append( "</h1>" );    		
		buf.append( "<p>" ).append( "blah blah blah..." ).append( "</p>" );  
		buf.append( "<p><img src=\"http://img0.bdstatic.com/img/image/shouye/xinshouye/huangyueji312.jpg\" /></p>" );
		// you can also reference a local image on your sdcard using the "content://s2p_localfile" provider (see below) 
		//buf.append( "<p><img src=\"content://s2p_localfile/sdcard/logo.gif\" /></p>" );
		buf.append( "</body>" );    		
		buf.append( "</html>" );

		PrintUtils.queueHtmlForPrinting( this, buf.toString() );
    }
    
    
    /**
     * Send html URL for printing.
     */
    void printHtmlUrlExample() {
		PrintUtils.queueHtmlUrlForPrinting( this, "http://www.baidu.com" );
    }
    
    
    /**
     * Send text file for printing.
     */
    void printTextFileExample(){
    	try{
	    	File f = PrintUtils.saveTextToTempFile( HELLO_WORLD );
	    	if( f != null ){
	    		PrintUtils.queueTextFileForPrinting( this, f );
	    	}
    	}
    	catch( Exception e ){
    		Log.e( TAG, "failed to save/queue text", e );
    	}
    }
    
    
    /**
     * ����һ��html�ļ����ڴ�ӡ
     */
    void printHtmlFileExample(){
    	try{
    		StringBuilder buf = new StringBuilder();
    		buf.append( "<html>" );
    		buf.append("<head>");
    		buf.append("<meta http-equiv= \" Content-Type \" content= \" text/html; charset=utf-8 \" />");
    		buf.append("</head>");
    		buf.append( "<body>" );
    		buf.append( "<h1>" ).append( HELLO_WORLD ).append( "</h1>" );    		
    		buf.append( "<p>" ).append( "��ӡ����ӡ��ҳ��������" ).append( "</p>" );  
    		buf.append( "<p><img src=\"http://img0.bdstatic.com/img/image/shouye/xinshouye/huangyueji312.jpg\" /></p>" );
    		// you can also reference a local image on your sdcard using the "content://s2p_localfile" provider (see below) 
    		//buf.append( "<p><img src=\"content://s2p_localfile/sdcard/logo.gif\" /></p>" );
    		buf.append( "</body>" );    		
    		buf.append( "</html>" );
    		
	    	File f = PrintUtils.saveHtmlToTempFile( buf.toString() );
	    	if( f != null ){
	    		PrintUtils.queueHtmlFileForPrinting( this, f );
	    	}
    	}
    	catch( Exception e ){
    		Log.e( TAG, "failed to save/queue html", e );
    	}
    }
    
    
}
