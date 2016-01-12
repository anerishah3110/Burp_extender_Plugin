package burp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Filechooser  extends JPanel 	{
		static JFileChooser fc;
//	static JTextArea log=new JTextArea(5, 20);
	public static void main(String args[])
	{
		JFrame j=new JFrame();
		fc= new JFileChooser();
		int rval= fc.showSaveDialog(j);
		if(rval==JFileChooser.APPROVE_OPTION)
		{
			File f =fc.getSelectedFile();
			
			
			//log.append("saving:"+ f.getName()+"."+"\n");
			
		}
		/*else
		{
			log.append("open command cancle by user \n");
		}
			log.setCaretPosition(log.getDocument().getLength());
		*/	/*j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			j.pack();
			j.setVisible(true);
*/	}
}
	


