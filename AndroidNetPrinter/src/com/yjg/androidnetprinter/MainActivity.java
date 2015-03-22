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
 * 各种打印类型的示例
 * @author yjg
 * @version 1.0
 */
public class MainActivity extends Activity
{
	//调试
	private static final String	TAG	= MainActivity.class.getSimpleName();
	
	static final String HELLO_WORLD = "打印的测试文字 + hello world +!@#$%^&*()_";
	
	
	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //如果需要检查并安装send2printer。
        // check for and install Send 2 Printer if needed
             
        if( PrintUtils.isSend2PrinterInstalled(this) == false ){
        	PrintUtils.launchMarketPageForSend2Printer( this );
        	return;
        }
        
        //设置GUI按钮
        //Button名片
        Button btnTestCanvas = (Button)findViewById( R.id.btnTestCanvas );
        btnTestCanvas.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printCanvasExample();
			}
        });
        //Button位图
        Button btnTestCanvasAsBitmap = (Button)findViewById( R.id.btnTestCanvasAsBitmap );
        btnTestCanvasAsBitmap.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printCanvasAsBitmapExample();
			}
        });
        //Button文字
        Button btnTestText = (Button)findViewById( R.id.btnTestText );
        btnTestText.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printTextExample();
			}
        });
        //BUtton网页
        Button btnTestHtml = (Button)findViewById( R.id.btnTestHtml );
        btnTestHtml.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printHtmlExample();
			}
        });
        //Button网址
        Button btnTestHtmlUrl = (Button)findViewById( R.id.btnTestHtmlUrl );
        btnTestHtmlUrl.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printHtmlUrlExample();
			}
        });
        //button文本文件
        Button btnTestTextFile = (Button)findViewById( R.id.btnTestTextFile );
        btnTestTextFile.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printTextFileExample();
			}
        });
        //Button网页文件
        Button btnTestHtmlFile = (Button)findViewById( R.id.btnTestHtmlFile );
        btnTestHtmlFile.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v){
				printHtmlFileExample();
			}
        });
    }
    
 
    /**
     * 为打印机发送名片视图
     * NOTE: Android 1.5 does not properly support drawBitmap() serialize/deserialize across process boundaries.
     * NOTE：1.5版本不完全支持drawBitmap()连载过分界线过程
     * 如果你需要显示bitmap，更多的资料请查阅{@link #printCanvasAsBitmapExample()}
     */
    void printCanvasExample(){
    	// 创建画布用来渲染
    	Picture picture = new Picture();
    	Canvas c = picture.beginRecording( 240, 240 );
    	// 用白色填充背景
    	c.drawRGB( 0xFF, 0xFF, 0xFF );
    	// 填充文字
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
    	
    	// 渲染图标
    	Bitmap icon = BitmapFactory.decodeResource( getResources(), R.drawable.icon );
    	c.drawBitmap( icon, 0, 0, null );

    	// 停止渲染
    	picture.endRecording();
    	
    	// 加入打印队列
    	File f = PrintUtils.saveCanvasPictureToTempFile( picture );
    	if( f != null ){
    		PrintUtils.queuePictureStreamForPrinting( this, f );
    	}
    }
    
    
    /**
     * 绘制一个bitmap 然后发送 bitmap 用于打印.
     */
    void printCanvasAsBitmapExample(){
    	// 创建画布用来渲染
    	Bitmap b = Bitmap.createBitmap( 240, 240, Bitmap.Config.RGB_565 );
    	Canvas c = new Canvas( b );
    	// 用白色填充背景
    	c.drawRGB( 0xFF, 0xFF, 0xFF );
    	// 填充文字
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
    	
    	// 渲染图标
    	Bitmap icon = BitmapFactory.decodeResource( getResources(), R.drawable.icon );
    	c.drawBitmap( icon, 0, 0, null );

    	// 加入渲染队列
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
     * 发送打印的文字
     */
    void printTextExample(){
		PrintUtils.queueTextForPrinting( this, HELLO_WORLD );
    }
    
    
    /**
     *  发送html用来打印
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
     * 发送一个html文件用于打印
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
    		buf.append( "<p>" ).append( "打印机打印网页测试内容" ).append( "</p>" );  
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
