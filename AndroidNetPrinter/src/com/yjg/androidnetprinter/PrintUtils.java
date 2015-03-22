package com.yjg.androidnetprinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
/**
 * ��ӡ��Ԫ�࣬��Ҫ�����ṩ��ӡ�ķ���
 * @version 1.0
 * @author yjg
 * @since 1.0
 */
public class PrintUtils
{
	// ��־
	private static final String	TAG	= PrintUtils.class.getSimpleName();
	// Send 2 Printer������
	private static final String PACKAGE_NAME = "com.rcreations.send2printer";
	
	// intent action to trigger printing
	public static final String PRINT_ACTION = "com.rcreations.send2printer.print";

	// content provider for accessing images on local sdcard from within html content
	// sample img src shoul be something like "content://s2p_localfile/sdcard/logo.gif"
	public static final String LOCAL_SDCARD_CONTENT_PROVIDER_PREFIX = "content://s2p_localfile";

	
	/**
	 * @return boolean :"Send 2 Printer" �Ƿ񱻰�װ
	 */
	public static boolean isSend2PrinterInstalled( Context context )
	{
		boolean output = false;
		PackageManager pm = context.getPackageManager();
        try { 
            PackageInfo pi = pm.getPackageInfo( PACKAGE_NAME, 0 );
            if( pi != null )
            {
            	output = true;
            }
        } catch (PackageManager.NameNotFoundException e) {}
        return output;
	}
	
	
	/**
	 * ��½Android Market ҳ������ Send 2 Printer 
	 * ������finish();
	 */
	public static void launchMarketPageForSend2Printer( final Activity context )
	{
        AlertDialog dlg = new AlertDialog.Builder( context )
        .setTitle("��װ Send 2 Printer")
        .setMessage("�������android�����ӡ֮ǰ��, ����Ҫ�Ӱ�׿�г���װ Send 2 Printer ")
        .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int which )
			{
				// launch browser
				Uri data = Uri.parse( "http://market.android.com/search?q=pname:" + PACKAGE_NAME );
				Intent intent = new Intent( android.content.Intent.ACTION_VIEW, data );
				intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity( intent );
				
				// exit
				context.finish();
			}
        }).show();	
	}
	
	
    /**
     * ���õ���ͼƬ����Ϊ�ļ����ڴ�ӡ
     */
    public static File saveCanvasPictureToTempFile( Picture picture )
    {
    	File tempFile = null;
    	    	
    	// save to temporary file
    	File dir = getTempDir();
    	if( dir != null )
    	{
			FileOutputStream fos = null;
			try
			{
				File f = File.createTempFile( "picture", ".stream", dir );
				fos = new FileOutputStream( f );
				picture.writeToStream( fos );
				tempFile = f;
			}
			catch( IOException e )
			{
				Log.e( TAG, "failed to save picture", e );
			}
			finally
			{
				close( fos );
			}
    	}		
    	
    	return tempFile;
    }
    
    
    /**
     * ���͵õ���ͼƬ�ļ����ڴ�ӡ (returned from {@link #saveCanvasPictureToTempFile}) .
     */
    public static boolean queuePictureStreamForPrinting( Context context, File f )
    {
    	// send to print activity
    	Uri uri = Uri.fromFile( f );
    	Intent i = new Intent( PRINT_ACTION );
    	i.setDataAndType( uri, "application/x-android-picture-stream" );
    	i.putExtra( "scaleFitToPage", true );
    	context.startActivity( i );
    	
    	return true;
    }
    
    
    /**
     *����õ���bitmap���ڴ�ӡ
     */
    public static File saveBitmapToTempFile( Bitmap b, Bitmap.CompressFormat format )
    throws IOException, UnknownFormatException
    {
    	File tempFile = null;
    	    	
    	// ���浽��ʱ�ļ���
    	File dir = getTempDir();
    	if( dir != null )
    	{
			FileOutputStream fos = null;
			try
			{
				String strExt = null;
				switch( format )
				{
					case PNG:
						strExt = ".pngx";
						break;
						
					case JPEG:
						strExt = ".jpgx";
						break;
						
					default:
						throw new UnknownFormatException( "unknown format: " + format );
				}
				File f = File.createTempFile( "bitmap", strExt, dir );
				fos = new FileOutputStream( f );
				b.compress( format, 100, fos );
				tempFile = f;
			}
			finally
			{
				close( fos );
			}
    	}		
    	
    	return tempFile;
    }
    
    
    /**
     * ���͵õ���ͼƬ������ӡ.
     */
    public static boolean queueBitmapForPrinting( Context context, File f, Bitmap.CompressFormat format )
    throws UnknownFormatException
    {
    	String strMimeType = null;
		switch( format )
		{
			case PNG:
				strMimeType = "image/png";
				break;
				
			case JPEG:
				strMimeType = "image/jpeg";
				break;
				
			default:
				throw new UnknownFormatException( "unknown format: " + format );
		}
    	
    	// ���ʹ�ӡ��activity
    	Uri uri = Uri.fromFile( f );
    	Intent i = new Intent( PRINT_ACTION );
    	i.setDataAndType( uri, strMimeType );
    	i.putExtra( "scaleFitToPage", true );
    	i.putExtra( "deleteAfterPrint", true );
    	context.startActivity( i );
    	
    	return true;
    }
    

    /**
     * ���͵õ�������������ӡ
     */
    public static boolean queueTextForPrinting( Context context, String strContent )
    {
    	Intent i = new Intent( PRINT_ACTION );
    	i.setType( "text/plain" );
    	i.putExtra( Intent.EXTRA_TEXT, strContent );
    	context.startActivity( i );
    	
    	return true;
    }
        
    
    /**
     * ���õ������ֱ��浽�ļ���������ӡ
     */
    public static File saveTextToTempFile( String text )
    throws IOException
    {
    	File tempFile = null;
    	    	
    	// ���浽��ʱ�ļ�
    	File dir = getTempDir();
    	if( dir != null )
    	{
			FileOutputStream fos = null;
			try
			{
				File f = File.createTempFile( "text", ".txt", dir );
				fos = new FileOutputStream( f );
				fos.write( text.getBytes() );
				tempFile = f;
			}
			finally
			{
				close( fos );
			}
    	}		
    	
    	return tempFile;
    }
    
    
    /**
     * ���͵õ����ı��ļ�������ӡ
     */
    public static boolean queueTextFileForPrinting( Context context, File f )
    {
    	// send to print activity
    	Uri uri = Uri.fromFile( f );
    	Intent i = new Intent( PRINT_ACTION );
    	i.setDataAndType( uri, "text/plain" );
    	i.putExtra( "deleteAfterPrint", true );
    	context.startActivity( i );
    	
    	return true;
    }
    
    
    /**
     * ���͵õ���html������ӡ.
     * 
     * You can also reference a local image on your sdcard using the "content://s2p_localfile" provider.
     * For example: <img src="content://s2p_localfile/sdcard/logo.gif">
     */
    public static boolean queueHtmlForPrinting( Context context, String strContent )
    {
    	// send to print activity
    	Intent i = new Intent( PRINT_ACTION );
    	i.setType( "text/html" );
    	i.putExtra( Intent.EXTRA_TEXT, strContent );
    	context.startActivity( i );
    	
    	return true;
    }
        
    
    /**
     * ���͵õ���html url ������ӡ.
     * 
     * You can also reference a local file on your sdcard using the "content://s2p_localfile" provider.
     * For example: "content://s2p_localfile/sdcard/test.html"
     */
    public static boolean queueHtmlUrlForPrinting( Context context, String strUrl )
    {
    	// send to print activity
    	Intent i = new Intent( PRINT_ACTION );
    	//i.setDataAndType( Uri.parse(strUrl), "text/html" );// this crashes!
    	i.setType( "text/html" );
    	i.putExtra( Intent.EXTRA_TEXT, strUrl );
    	context.startActivity( i );
    	
    	return true;
    }
        
    
    /**
     * ����õ���html���ݵ��ļ������ڴ�ӡ
     */
    public static File saveHtmlToTempFile( String html )
    throws IOException
    {
    	File tempFile = null;
    	    	
    	// save to temporary file
    	File dir = getTempDir();
    	if( dir != null )
    	{
			FileOutputStream fos = null;
			try
			{
				File f = File.createTempFile( "html", ".html", dir );
				fos = new FileOutputStream( f );
				fos.write( html.getBytes() );
				tempFile = f;
			}
			finally
			{
				close( fos );
			}
    	}		
    	
    	return tempFile;
    }
    
    
    /**
     * ���͵õ���html�ļ�������ӡ
     */
    public static boolean queueHtmlFileForPrinting( Context context, File f )
    {
    	// send to print activity
    	Uri uri = Uri.fromFile( f );
    	Intent i = new Intent( PRINT_ACTION );
    	i.setDataAndType( uri, "text/html" );
    	i.putExtra( "deleteAfterPrint", true );
    	context.startActivity( i );
    	
    	return true;
    }
    
    
	/**
	 * ����һ����ʱ�ļ�·������sd���������ż�����ӡ���ļ���
	 * 
	 * @return null �����ʱ�ļ��в��ܱ������򷵻ؿ�
	 * @author yjg
	 * @version 1.0
	 *  
	 */
	public static File getTempDir()
	{
		File dir = new File( Environment.getExternalStorageDirectory(), "temp" );
		if( dir.exists() == false && dir.mkdirs() == false )
		{
			Log.e( TAG, "failed to get/create temp directory" );
			return null;
		}
		return dir;
	}
		
    
    /**
     * �ر�io�������������쳣
     * Helper method to close given output stream ignoring any exceptions.
     */
    public static void close( OutputStream os )
    {
        if( os != null )
        {
            try
            {
                os.close();
            }
            catch( IOException e ) {}
        }
    }
    

    /**
     * �׳�һ��δ֪�����쳣
     * Thrown by bitmap methods where the given Bitmap.CompressFormat value is unknown.
     */
    public static class UnknownFormatException extends Exception
    {
    	public UnknownFormatException( String msg )
    	{
    		super( msg );
    	}
    }
}
