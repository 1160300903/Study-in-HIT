import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

class MyExpression
{
	public ArrayList<MyExpression> expressions=new ArrayList<MyExpression>();
	private ArrayList<String> strings=new ArrayList<String>();
	private ArrayList<String> operators=new ArrayList<String>();
	private static final Font[] fonts={new Font("Arial",0,30),new Font("Arial",0,15),new Font("Arial",0,10)};
	private static final int BORDER=3,HEIGHT=30;
	//********根号还不能画出
	//需要处理的符号：
	//替换：int sum root
	//图像处理（优先级升序）：(^) /
	//比/优先级低的符号：=|+|-|sin|cos|tan|cot|sec|csc|sinh|cosh|tanh|coth|sech|csch|log|ln|lg|\\{|\\}|\\(|\\)|\\|

	MyExpression(){}

	public static void main(String[] args)
	{
		Scanner s=new Scanner(System.in);
		MyExpression abc=new MyExpression();
		if(!abc.read(s.nextLine()))
		{
			System.out.println("格式有误");
			return;
		}
		abc.toImage("test");
	}

	/*//判断一个表达式是否可以去括号
	private boolean peelable(char o)
	{
		if(o=='/')
		{
			if(expressions.size()==2&&operators.get(0)=='/')
				return false;
			return true;
		}
		int i;
		if(expressions.size()==0)
		{
			String a=strings.get(0);
			if((a.charAt(0)=='('&&a.charAt(a.length()-1)==')')||(a.charAt(0)=='{'&&a.charAt(a.length()-1)=='}')||(a.charAt(0)=='|'&&a.charAt(a.length()-1)=='|'))
				return true;
			for(i=0;i<a.length();i++)
			{
				if(a.charAt(i)=='+') return false;
				if(a.charAt(i)=='-') return false;
				if(a.charAt(i)=='*') return false;
				if(a.charAt(i)=='=') return false;
			}
			if(a.indexOf("sin")>=0) return false;
			if(a.indexOf("cos")>=0) return false;
			if(a.indexOf("tan")>=0) return false;
			if(a.indexOf("cot")>=0) return false;
			if(a.indexOf("sec")>=0) return false;
			if(a.indexOf("csc")>=0) return false;
			if(a.indexOf("sinh")>=0) return false;
			if(a.indexOf("cosh")>=0) return false;
			if(a.indexOf("tanh")>=0) return false;
			if(a.indexOf("coth")>=0) return false;
			if(a.indexOf("sech")>=0) return false;
			if(a.indexOf("csch")>=0) return false;
			if(a.indexOf("log")>=0) return false;
			if(a.indexOf("ln")>=0) return false;
			if(a.indexOf("lg")>=0) return false;
			return true;
		}
		else if(expressions.size()==1)
			return expressions.get(0).peelable();
		else
		{
			for(i=0;i<operators.size();i++)
				if(operators.get(i)=="/") return false;
			return true;
		}
	}*/

	public boolean read(String e)
	{
		return read(e,true);
	}

	private boolean read(String e,boolean peelable)
	{
		int i,index,cur=0,first=-1,last=-1;
		boolean ok=true;
		boolean peeled=false;
		int hasOuterBracket=outerBracket(e);
		boolean nextpeelable=false;
		//中文字符变英文字符
		
		//括号配对查错及去最外层括号
		switch(hasOuterBracket)
		{
			case 1:
				e=e.substring(1,e.length()-1);
				peeled=true;
				break;
			case -1:
				return false;
		}
		
		//寻找^
		index=getOuterCharIndex('^',e,true);

		//找到^，开始处理
		if(index>=0)
		{
			operators.add("^");
			strings.add(e.substring(0,index));
			expressions.add(new MyExpression());
			ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
			if(!ok) return false;
			strings.add(e.substring(index+1,e.length()));
			expressions.add(new MyExpression());
			ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
			if(!ok) return false;
			//运算符个数查错
			return operators.size()<expressions.size();
		}

		//没有读到^，开始尝试/
		index=getOuterCharIndex('/',e,false);

		//找到外层/，开始处理
		if(index>=0)
		{
			int l=getLeftBorder(e,index);
			if(l==-1) return false;
			int r=getRightBorder(e,index);
			if(r==-1) return false;

			//加括号
			if(hasOuterBracket==1&&!peelable)
			{
				expressions.add(new MyExpression());
				expressions.get(expressions.size()-1).read("");
				operators.add("(");
			}
			if(l>0)
			{
				strings.add(e.substring(0,l));
				expressions.add(new MyExpression());
				operators.add("");
				ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),false);
				if(!ok) return false;
			}
			expressions.add(new MyExpression());
			if(l==0&&r>=e.length()-1)
			{
				strings.add(e.substring(l,index));
				ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),true);
				operators.add("/");
				expressions.add(new MyExpression());
				strings.add(e.substring(index+1,r));
				ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),true);
			}
			else
			{
				strings.add(e.substring(l,r));
				ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),false);
			}
			if(!ok) return false;
			if(r<e.length()-1)
			{
				strings.add(e.substring(r,e.length()));
				operators.add("");
				expressions.add(new MyExpression());
				ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),false);
				if(!ok) return false;
			}
			//加括号
			if(hasOuterBracket==1&&!peelable)
			{
				operators.add(")");
				expressions.add(new MyExpression());
				expressions.get(expressions.size()-1).read("");
			}
			//运算符个数查错
			return operators.size()<expressions.size();
		}

		//没有找到外层/，开始读括号
		cur=0;
		first=-1;
		for(i=0;i<e.length();i++)
		{
			if(e.charAt(i)=='(')
			{
				if(first==-1) first=i;
				cur++;
			}
			else if(e.charAt(i)==')')
				if(--cur==0)
				{
					last=i+1;
					break;
				}
		}
		if(first==-1)
		{
			//未找到括号
			if(hasOuterBracket==1&&!peelable)
				strings.add("("+e+")");
			else
				strings.add(e);
			return true;
		}
		else
		{
			//确定是否为纯分式
			if((index=getOuterCharIndex('/',e.substring(first,last),true))>0)
				if(getLeftBorder(e,index)==first+1&&getRightBorder(e,index)==last)
				{
					//寻找是否有紧邻的(^)
					if(last<e.length()&&e.charAt(last)=='(')
					{
						cur=1;
						for(i=last+1;i<e.length();i++)
							if(e.charAt(i)=='^'&&cur==1) break;
							else if(e.charAt(i)=='(') cur++;
							else if(e.charAt(i)==')') cur--;
						if(!(i<e.length()&&e.charAt(i)=='^'&&cur==1)) nextpeelable=true;
					}
				}
			//加括号
			if(hasOuterBracket==1&&!peelable)
			{
				expressions.add(new MyExpression());
				expressions.get(expressions.size()-1).read("");
				operators.add("(");
			}
			if(first>0)
			{
				expressions.add(new MyExpression());
				strings.add(e.substring(0,first));
				operators.add("");
				ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),false);
				if(!ok) return false;
			}
			strings.add(e.substring(first,last));
			expressions.add(new MyExpression());
			ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),nextpeelable);
			if(!ok) return false;
			if(last<e.length())
			{
				operators.add("");
				expressions.add(new MyExpression());
				strings.add(e.substring(last,e.length()));
				ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1),false);
				if(!ok) return false;
			}
			//加括号
			if(hasOuterBracket==1&&!peelable)
			{
				operators.add(")");
				expressions.add(new MyExpression());
				expressions.get(expressions.size()-1).read("");
			}
			//运算符个数查错
			return operators.size()<expressions.size();
		}
	}

	private int getWidth(int divide)
	{
		//如果到达最底部则直接统计字符串长度
		if(expressions.size()==0)
			return getWidth(strings.get(0))/divide;

		//如果只有一个表达式
		if(expressions.size()==1)
			return expressions.get(0).getWidth(divide);

		int sum=0;
		boolean last=false;//记录最后一个表达式是否被统计过
		for(int i=0;i!=operators.size();i++)
		{
			sum+=getWidth(operators.get(i))/divide;
			if(operators.get(i)=="/")
			{
				last=true;
				sum+=Math.max(expressions.get(i).getWidth(divide),expressions.get(i+1).getWidth(divide));
			}
			else if(operators.get(i)=="^")
			{
				last=true;
				sum+=Math.max(expressions.get(i).getWidth(1),expressions.get(i+1).getWidth(1))/(divide<3?(divide+1):divide);
			}
			else
			{
				last=false;
				sum+=expressions.get(i).getWidth(divide);
				continue;
			}
			if(++i!=operators.size())
				sum+=getWidth(operators.get(i))/divide;
			else break;
		}

		//如果最后一个表达式未被统计过
		if(!last)
			sum+=expressions.get(expressions.size()-1).getWidth(divide);
		return sum;
	}

	private int getHeight(int divide)
	{
		if(expressions.size()==0) return getHeight(strings.get(0))/divide;
		int max=expressions.get(0).getHeight(divide);
		for(int i=0;i!=operators.size();i++)
		{
			if(operators.get(i)=="/")
				max=Math.max(max,expressions.get(i).getHeight(divide)+expressions.get(i+1).getHeight(divide)+BORDER*2);
			else if(operators.get(i)=="^")
				max=Math.max(max,expressions.get(i).getHeight(divide)+expressions.get(i+1).getHeight(divide));
			else
			{
				max=Math.max(max,expressions.get(i+1).getHeight(divide));
				max=Math.max(max,getHeight(operators.get(i))/divide);
			}
		}
		return max;
	}

	public String toString()
	{
		String a=strings.get(0);
		Iterator<String> it1=strings.iterator();
		it1.next();
		Iterator<String> it2=operators.iterator();
		while(it1.hasNext()&&it2.hasNext())
			a+=it2.next()+it1.next();
		return a;
	}

	public BufferedImage toImage(String filename)
	{
		int w=getWidth(1),h=getHeight(1);
		int inity;
		BufferedImage b=new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g=b.createGraphics();
		g.setFont(fonts[0]);
		g.setColor(new Color(0,0,0));
		g.setBackground(new Color(255,255,255));
		g.clearRect(0,0,w,h);
		draw(g,2,h/2+HEIGHT/3,1);
		try
		{
			ImageIO.write(b, "jpg", new File("D:\\"+filename+".jpg"));
		}
		catch(IOException e)
		{
			System.out.println("写入图片出错！");//写入图片出错
		}
		return b;
	}

	private void draw(Graphics2D g,int x,int y,int divide)
	{
		//如果到达最底部则绘制字符串
		if(expressions.size()==0)
		{
			g.setFont(fonts[divide-1]);
			g.drawString(strings.get(0),x,y);
			//System.out.println(strings.get(0)+":"+x);
			return;
		}

		//如果只有一个表达式
		if(expressions.size()==1)
		{
			expressions.get(0).draw(g,x,y,divide);
			return;
		}
		
		//对所有表达式依次绘图
		boolean last=false;//记录最后一个表达式是否被画过
		for(int i=0;i!=operators.size();i++)
		{
			if(operators.get(i)=="/")
			{
				expressions.get(i).draw(g,x,y-BORDER/divide-expressions.get(i).getHeight(divide)/2,divide);
				g.drawLine(x,y-HEIGHT/(divide*3),
						   x+Math.max(expressions.get(i).getWidth(divide),expressions.get(i+1).getWidth(divide)),y-HEIGHT/(divide*3));
				expressions.get(i+1).draw(g,x,y+BORDER/divide+expressions.get(i+1).getHeight(divide)/2,divide);
				last=true;
				x+=Math.max(expressions.get(i).getWidth(divide),expressions.get(i+1).getWidth(divide));
			}
			else if(operators.get(i)=="^")
			{
				expressions.get(i).draw(g,x,y+expressions.get(i).getHeight(divide<3?(divide+1):divide)-HEIGHT/(divide*2),(divide<3?(divide+1):divide));
				expressions.get(i+1).draw(g,x,y-HEIGHT/(2*divide),(divide<3?(divide+1):divide));
				last=true;
				x+=Math.max(expressions.get(i).getWidth(1),expressions.get(i+1).getWidth(1))/(divide<3?(divide+1):divide);
			}
			else
			{
				expressions.get(i).draw(g,x,y,divide);
				x+=expressions.get(i).getWidth(divide);
				last=false;
				g.setFont(fonts[divide-1]);
				g.drawString(operators.get(i),x,y);
				//System.out.println(operators.get(i)+":"+x);
				x+=getWidth(operators.get(i));
				continue;
			}
			if(++i!=operators.size())
			{
				g.setFont(fonts[divide-1]);
				g.drawString(operators.get(i),x,y);
				//System.out.println(operators.get(i)+":"+x);
				x+=getWidth(operators.get(i))/divide;
			}
			else break;
		}

		//如果最后一个表达式未被画过则绘制
		if(!last)
			expressions.get(expressions.size()-1).draw(g,x,y,divide);
	}

	private static int getWidth(String a)
	{
		if(a=="") return 0;
		char[] charArray=a.toCharArray();
		int sum=0;
		for(int i=0;i<charArray.length;i++)
			sum+=getWidth(charArray[i]);
		return sum;
	}

	private static int getHeight(String a)
	{
		return HEIGHT;
	}

	private static int getWidth(char a)
	{
		if(a=='/'||a=='f'||a=='l'||a=='i'||a=='j'||a=='I'||a=='r'||a=='t')
			return 7;
		else if(a>='A'&&a<='Z'&&a!='I'&&a!='M'&&a!='W')
			return 21;
		else if(a=='%'||a=='M'||a=='m'||a=='w')
			return 26;
		else if(a=='W')
			return 31;
		else if(a=='∫')
			return 10;
		else
			return 17;
	}

	private static int getHeight(char a)
	{
		return 30;
	}

	private static int getOuterCharIndex(char a,String e,boolean fromhead)
	{
		int cur=0,index;
		if(fromhead)
		{
			for(index=0;index<e.length();index++)
			{
				if(e.charAt(index)=='(') cur++;
				else if(e.charAt(index)==')') cur--;
				if(cur>0) continue;
				if(e.charAt(index)==a) return index;
			}
		}
		else
		{
			for(index=e.length()-1;index>=0;index--)
			{
				if(e.charAt(index)=='(') cur--;
				else if(e.charAt(index)==')') cur++;
				if(cur>0) continue;
				if(e.charAt(index)==a) return index;
			}
		}
		return -1;
	}

	private static int getLeftBorder(String a,int cur)
	{
		int i=cur,k=0;
		char tmp;
		while(--i>=0)
		{
			tmp=a.charAt(i);
			if(tmp=='(') if(k>0) {k--; continue;} else return -1;
			if(tmp==')') {k++; continue;}
			if(k>0) continue;
			if((tmp>='a'&&tmp<='z')||(tmp>='A'&&tmp<='Z')||(tmp>='0'&&tmp<='9')||tmp=='.'||tmp=='*'||tmp=='/'||tmp=='!'||tmp=='?') continue;
			break;
		}
		if(k>0) return -1;
		return i+1;
	}

	private static int getRightBorder(String a,int cur)
	{
		int i=cur,k=0;
		char tmp;
		while(++i<a.length())
		{
			tmp=a.charAt(i);
			if(tmp=='(') {k++; continue;}
			if(tmp==')') if(k>0) {k--; continue;} else return -1;
			if(k>0) continue;
			if((tmp>='a'&&tmp<='z')||(tmp>='A'&&tmp<='Z')||(tmp>='0'&&tmp<='9')||tmp=='.'||tmp=='*'||tmp=='/'||tmp=='!'||tmp=='?') continue;
			break;
		}
		if(k>0) return -1;
		return i;
	}

	private static int outerBracket(String e)
	{
		//括号配对查错，返回是否有最外层括号
		int first=-1,last=-1,i,cur=0;
		for(i=0;i<e.length();i++)
		{
			if(e.charAt(i)=='(')
			{
				if(first==-1) first=i;
				cur++;
			}
			else if(e.charAt(i)==')')
				if(cur<=0) return -1;
				else if (cur--==1)
				{
					last=i+1;
					break;
				}
		}
		if(first==0&&last==e.length())
			return 1;
		return 0;
	}
}