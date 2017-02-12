package gui;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Dimension;
import java.util.ArrayList;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import util.MyMessage;
import util.AudioTools;
import util.stopListener;
import NetEvent.Client;
import bin.test;

public class ChattingBox extends JPanel
{
	private ChattingBoxRightAction rightAction=new ChattingBoxRightAction();
	private ChattingBoxHyperlinkListener hyperlinkAction=new ChattingBoxHyperlinkListener();
	private boolean onHyperlink=false;
	private String mouseHyperlink;
	private String currentHyperlink;

	public JTextPane myPane=new JTextPane();
	private JScrollPane myScroll=new JScrollPane(myPane);
	private StringBuilder html=new StringBuilder("");

	private JPopupMenu textMenu=new JPopupMenu();
	private JPopupMenu userMenu=new JPopupMenu();
	private JMenuItem copy=new JMenuItem("复制");
	private JMenuItem paste=new JMenuItem("粘贴");
	private JMenuItem reset=new JMenuItem("清屏");
	private JMenuItem abspeak=new JMenuItem("禁言");
	private JMenuItem getInfo=new JMenuItem("个人资料");

	private static final String CLASSPATH=ChattingBox.class.getResource("").getPath();
	private static final String PROPATH="file:"+CLASSPATH;
	private static final String PROPICTPATH="file:"+test.PICTPATH;
	private static final String PROFILEPATH="file:"+test.FILEPATH;

	public static ChattingBox c;
	public static final void main(String[] args)
	{
		JFrame f=new JFrame("myApplication");
		c=new ChattingBox();
		f.getContentPane().add(c);
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);
		Client client = new Client();
		Thread netThread = new Thread(client);
		netThread.start();
		try {
			client.launchRequest("xy16", "123456");
			client.enterQuestion("1");
			client.sendContent("hello", new ArrayList<String>(),"1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(c.myPane.getText());
	}

	ChattingBox()
	{
		myPane.setPreferredSize(new Dimension(500,550));
		myPane.setContentType("text/html");
		myPane.setEditable(false);

		myPane.addMouseListener(new ChattingBoxMouseListener());
		myPane.addHyperlinkListener(hyperlinkAction);
		textMenu.add(copy);
		copy.addActionListener(rightAction);
		textMenu.add(reset);
		reset.addActionListener(rightAction);
		userMenu.add(getInfo);
		getInfo.addActionListener(rightAction);
		myPane.add(textMenu);
		myPane.add(userMenu);

		this.add(myScroll);
	}

	public void pushMessage(boolean ismyself,String message,ArrayList<String> pictures)
	{
		boolean ismyself=msg.userName=="a";//Personal.username;
		msg.message=msg.message.replaceAll("\n","<br>");
		if(msg.pictures!=null)
			for(int i=0;i<msg.pictures.size();i++)
			{
				if(!(new File(test.PICTPATH+msg.pictures.get(i)).exists()))
					{}//调用网络接口下载图片，下载完成时刷新
				msg.message=msg.message.replaceAll("[^%]%"+i+" ",
					"<a href=\"pict:"+PROPICTPATH+msg.pictures.get(i)+"\">"+
					"<img border=\"0\" src=\""+PROPICTPATH+msg.pictures.get(i)+"\" "+
					"alt=\"正在加载图片\"></a>");
				msg.message=msg.message.replaceAll("%%","%");
			}
		html.append("<p align=\"center\">"+msg.messageTime+"</p>"+
			"<table border=\"0\" white-space=\"0\" "+
			"align=\""+(ismyself?"right":"left")+"\" "+
			"cellspacing=\"0\" cellpadding=\"0\" "+
			"style=\"font-size:0;-webkit-user-select:none;"+
			"-moz-user-select:none;"+
			"-ms-user-select:none;user-select:none;\">"+
			"<tr><td rowspan=\"3\">"+
			(ismyself?"":"<a href=\"user:用户名A\">"+
			"<img src=\""+PROPATH+"ask.jpg\"></a>")+
			"</td>"+
			"<td><img src=\""+PROPATH+"bubble_lu.jpg\"></td>"+
			"<td style=\"background-image:url("+PROPATH+"bubble_up.jpg);"+
			"background-repeat:repeat-x;\">&nbsp;</td>"+
			"<td><img src=\""+PROPATH+"bubble_ru.jpg\"></td>"+
			"<td rowspan=\"3\">"+
			(ismyself?"<a href=\"user:用户名B\">"+
			"<img src=\""+PROPATH+"ask.jpg\"></a>":"")+
			"</td></tr>"+
			"<tr><td style=\"background-image:url("+PROPATH+"bubble_le.jpg)\">&nbsp;</td>"+
			"<td style=\"-webkit-user-select:text;"+
			"-moz-user-select:text;-ms-user-select:text;"+
			"user-select:text;font-size:12px;\">"+message+"</td>"+
			"<td style=\"background-image:url("+PROPATH+"bubble_ri.jpg)\">&nbsp;</td></tr>"+
			"<tr><td><img src=\""+PROPATH+"bubble_ld.jpg\"></td>"+
			"<td style=\"background-image:url("+PROPATH+"bubble_do.jpg)\">&nbsp;</td>"+
			"<td><img src=\""+PROPATH+"bubble_rd.jpg\"></td></tr></table><br>");
		myPane.setText(html.toString());
		myPane.setSelectionStart(myPane.getText().length());
	}

	private class ChattingBoxMouseListener implements MouseListener
	{
		ChattingBoxMouseListener(){}
		public void mousePressed(MouseEvent e)
		{
			if(onHyperlink) currentHyperlink=mouseHyperlink;
			if(e.getButton()==MouseEvent.BUTTON3)
			{
				//判断用户身份
				if(onHyperlink)
				{
					//user.add(abspeak)
					//abspeak.setEnabled(true);
					getInfo.setEnabled(true);
					reset.setEnabled(true);
					userMenu.show(ChattingBox.this,e.getX(),e.getY());
				}
				else
				{
					copy.setEnabled(true);
					reset.setEnabled(true);
					textMenu.show(ChattingBox.this,e.getX(),e.getY());
				}
			}
		}
		public void mouseReleased(MouseEvent e){}
		public void mouseClicked(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
	}

	private class ChattingBoxRightAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("复制"))
			{
				if(myPane.getSelectionStart()!=myPane.getSelectionEnd())
					myPane.copy();
			}
			else if(e.getActionCommand().equals("清屏"))
			{
				html.delete(0,html.length());
				myPane.setText("");
			}
		}
	}

	private class ChattingBoxHyperlinkListener implements HyperlinkListener
	{
		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			if(e.getEventType()==HyperlinkEvent.EventType.ENTERED)
			{
				onHyperlink=true;
				mouseHyperlink=e.getDescription();
			}
			else if(e.getEventType()==HyperlinkEvent.EventType.EXITED)
				onHyperlink=false;
			else
			{
				if(currentHyperlink.substring(0,4).equals("user"))
				{
					//利用currentHyperlink.substring(5)打开个人资料
				}
				else if(cmd.equals("pict"))
				{
					//利用图片框打开大图
				}
				else if(cmd.equals("audi"))
				{
					//System.out.println("激活超链接："+currentHyperlink);
					String tmpstr;
					if(AudioTools.isPlaying())
					{
						tmpstr="<a href=\"audi:"+
							AudioTools.getCurrentPlayingAudio()+"\">"+
							"<img src=\""+PROPATH+"button_stop.gif";
						int tmpindex=html.lastIndexOf(tmpstr);
						tmpindex+=tmpstr.length()-8;
						html.replace(tmpindex,tmpindex+4,"play");
					}
					tmpstr="<a href=\""+currentHyperlink+"\">"+
						"<img src=\""+PROPATH+"button_play.gif";
					int tmpindex=html.lastIndexOf(tmpstr);
					tmpindex+=tmpstr.length()-8;
					html.replace(tmpindex,tmpindex+4,"stop");
					myPane.setText(html.toString());
					AudioTools.playAudio(
						AudioTools.CLASSPATH+currentHyperlink.substring(5),
						new stopListener()
						{
							public void stop()
							{
								String tmpstr="<a href=\"audi:"+
									AudioTools.getCurrentPlayingAudio()+"\">"+
									"<img src=\""+PROPATH+"button_stop.gif";
								int tmpindex=html.lastIndexOf(tmpstr);
								tmpindex+=tmpstr.length()-8;
								html.replace(tmpindex,tmpindex+4,"play");
								myPane.setText(html.toString());
							}
						});
				}
				else if(cmd.equals("file"))
				{
					File f;
					if((f=new File(test.FILEPATH+currentHyperlink.substring(5))).exists())
					{
						java.awt.Desktop.getDesktop().open(f);
						return;
					}
					//下载文件
				}
			}
		}
	}
}