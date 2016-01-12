package burp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.*;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.lobobrowser.util.NameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;

import com.foundationdb.tuple.ByteArrayUtil;

public class BurpExtender extends AbstractTableModel implements IBurpExtender,
		ITab, IHttpListener, IMessageEditorController {
	private IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	private JSplitPane splitPane;
	private JSplitPane splitPane1;
	int Hor = JSplitPane.HORIZONTAL_SPLIT;

	int ver = JSplitPane.VERTICAL_SPLIT;
	private ITextEditor ref;

	private ITextEditor requestViewer;
	private ITextEditor responseViewer;
	private ITextEditor map;
	private ITextEditor persistent;
	private ITextEditor xss;;
	private String R = "";
	private String x = "";
	

	private final List<LogEntry> log = new ArrayList<LogEntry>();
	// public static String wo[] =new String[5000];

	public static ArrayList wo = new ArrayList();
	private ArrayList l = new ArrayList();
	private ArrayList url = new ArrayList();
	private ArrayList temp1 = new ArrayList();
	// private ArrayList response=new ArrayList();
	// private ArrayList request=new ArrayList();
	private ArrayList urlpost = new ArrayList();
	private LinkedList<String>[] link = new LinkedList[10000];
	private static int d = 0;
	private int setting;
	private ArrayList MainUrl = new ArrayList();
	private ArrayList methodlist = new ArrayList();
	private ArrayList MainUrl1 = new ArrayList();
	private JComboBox<String> combo = new JComboBox();
	private JButton remove = new JButton("Remove");
	private JButton add = new JButton("Add");
	private JButton dump = new JButton("Dump");
	private JButton analysis = new JButton("Analysis");
	private JButton per = new JButton("Persistent");
	private JButton abc = new JButton("abc");

	private JLabel lable = new JLabel("Add to scope");
	private JTextField text = new JTextField(30);

	private JPanel p = new JPanel();
	String uu = null;
	private List<String> scope = new ArrayList<String>();
	private ArrayList scopeurl = new ArrayList();
	private IHttpRequestResponse currentlyDisplayedItem;
	Table logTable;
	private static int c = 0;
	private static int s = 0;
	private static int a1 = 0;
	byte[] q;
	File file;
	File file1;

	public BurpExtender() {
		remove.addActionListener(new CustomActionListener());
		add.addActionListener(new CustomActionListener());
		analysis.addActionListener(new CustomActionListener());
		dump.addActionListener(new CustomActionListener());
		per.addActionListener(new CustomActionListener());

		
			 
						 file1 = new File("C:/Users/Public/Rules.xml");

			// if file doesnt exists, then create it
			if (!file1.exists()) {
				try {
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			 
					// root elements
					Document doc = docBuilder.newDocument();
					Element rootElement = doc.createElement("company");
					doc.appendChild(rootElement);
			 
					// staff elements
					Element cat = doc.createElement("category");
					rootElement.appendChild(cat);
			 
				
					Element vul = doc.createElement("vulnerability");
					vul.appendChild(doc.createTextNode("Path"));
					cat.appendChild(vul);
			 
					// lastname elements
					Element key = doc.createElement("keywords");
					key.appendChild(doc.createTextNode("txtSearch"));
					cat.appendChild(key);
			 
					
			 
					// write the content into xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(file1);
					transformer.transform(source, result);
					 
					System.out.println("File saved!");

					file1.createNewFile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
				

		
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			 
		  } 
		
		
	

	//
	// implement IBurpExtender
	//

	@Override
	public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks) {
		// keep a reference to our callbacks object
		this.callbacks = callbacks;

		// obtain an extension helpers object
		helpers = callbacks.getHelpers();

		// set our extension name
		callbacks.setExtensionName("Star");

		// create our UI
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// main split pane

				// table of log entries
				logTable = new Table(BurpExtender.this);
				logTable.setSize(700, 500);
				logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				logTable.getColumnModel().getColumn(0).setPreferredWidth(30);
				logTable.getColumnModel().getColumn(1).setPreferredWidth(80);
				logTable.getColumnModel().getColumn(2).setPreferredWidth(620);
				logTable.getColumnModel().getColumn(3).setPreferredWidth(80);
				logTable.getColumnModel().getColumn(4).setPreferredWidth(80);

				JScrollPane scrollPane = new JScrollPane(logTable);
				Dimension d = new Dimension(900, 400);
				scrollPane.setPreferredSize(d);
				logTable.addMouseListener(new PopClickListener());

				// splitPane.setLeftComponent(scrollPane);}

				// tabs with request/response viewers
				JTabbedPane tabs = new JTabbedPane();
				requestViewer = callbacks.createTextEditor();
				responseViewer = callbacks.createTextEditor();
				map = callbacks.createTextEditor();
				ref = callbacks.createTextEditor();
				persistent = callbacks.createTextEditor();
				xss = callbacks.createTextEditor();
				// rv=callbacks.createMessageEditor(BurpExtender.this, false);
				tabs.addTab("Params", requestViewer.getComponent());
				tabs.addTab("Data", responseViewer.getComponent());
				tabs.addTab("Map", map.getComponent());
				tabs.addTab("Reflection Analysis", ref.getComponent());
				tabs.addTab("Persistent Analysis", persistent.getComponent());
				tabs.addTab("Vulnerability", xss.getComponent());
				combo.setMaximumRowCount(5);
				Dimension d1 = new Dimension(200, 25);
				combo.setPreferredSize(d1);
				p.add(lable);
				p.add(text);
				p.add(add);
				p.add(combo);
				p.add(remove);

				p.add(analysis);
				p.add(dump);
				p.add(per);
		
				splitPane = new JSplitPane(Hor, false, scrollPane, p);

				splitPane1 = new JSplitPane(ver, splitPane, tabs);
				// splitPane.setDividerLocation(2.5);
				// splitPane.setRightComponent(tabs);

				// customize our UI components
				callbacks.customizeUiComponent(splitPane1);
				callbacks.customizeUiComponent(logTable);
				callbacks.customizeUiComponent(scrollPane);
				callbacks.customizeUiComponent(tabs);

				// add the custom tab to Burp's UI
				callbacks.addSuiteTab(BurpExtender.this);

				// register ourselves as an HTTP listener
				callbacks.registerHttpListener(BurpExtender.this);
			}
		});

	}

	//
	// implement ITab
	//

	@Override
	public String getTabCaption()

	{
		return "Star";
	}

	@Override
	public Component getUiComponent() {
		return splitPane1;
	}

	//
	// implement IHttpListener
	//

	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest,
			IHttpRequestResponse messageInfo) {
		String ll = "";
		R = "";
		x="";
		
		int count = 0;
		String hn = messageInfo.getHost();

		try {

			if (!messageIsRequest)

			{

				if (scopeurl.contains(hn) || scopeurl.isEmpty()) {
					s++;

					byte[] nl = messageInfo.getResponse();
					String res1 = new String(nl, Charset.forName("UTF-8"));

					System.out.println("in ifffffffffffffff");
					String method = helpers.analyzeRequest(messageInfo)
							.getMethod();
				//	methodlist.add(method);

					byte[] e1 = messageInfo.getRequest();
					String ee = new String(e1, Charset.forName("UTF-8"));
					
					String temp2="";
					// request.add(e1);
					// response.add(res1);

					try {

						file = new File("C:/Users/Public/my1.txt");

						// if file doesnt exists, then create it
						if (!file.exists()) {
							file.createNewFile();
						}

						FileWriter fw = new FileWriter(file.getAbsoluteFile(),
								true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(ee);
						bw.close();

					} catch (IOException e) {

					}

					// create a new log entry with the message details
					/*
					 * synchronized(log) {
					 * 
					 * int row = log.size(); log.add(new LogEntry(toolFlag,
					 * helpers.analyzeRequest(messageInfo).getMethod(),
					 * callbacks.saveBuffersToTempFiles(messageInfo),
					 * helpers.analyzeRequest
					 * (messageInfo).getUrl(),helpers.analyzeRequest
					 * (messageInfo).getParameters(),nl));
					 * 
					 * 
					 * fireTableRowsInserted(row, row); }
					 */

					System.out.println("hhhhhhhhhhhhhh");
					  NodeList nodeLst = null;
			
					  
					try
					{
						
					//		 file1 = new File(conf);
						
					  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					  DocumentBuilder db;
					
						db = dbf.newDocumentBuilder();
					  Document doc = db.parse(file1);
					  doc.getDocumentElement().normalize();
					  System.out.println("Root element " + doc.getDocumentElement().getNodeName());
					   nodeLst = doc.getElementsByTagName("category");
					  System.out.println("Information of all categories");
					}
						 catch (Exception e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
					  

					if (method.equalsIgnoreCase("GET")) {
						URL a = helpers.analyzeRequest(messageInfo).getUrl();

						String s = a.toString();
						// System.out.println(s);
						String[] qw;
						qw = s.split("\\?");
						String oo = qw[0];
						
						System.out.println("Get URL:" + oo);
						List<org.apache.http.NameValuePair> p1 = null;
						try {
							p1 = URLEncodedUtils.parse(new URI(s), "UTF-8");
							// System.out.println(p.toString());
						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						for (org.apache.http.NameValuePair param1 : p1) {
							
							String s11 = param1.getName();
							String s22 = param1.getValue();
							for (int l = 0; l < nodeLst.getLength(); l++) {

							    Node fstNode = nodeLst.item(l);
							    
							    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
							  
							           Element fstElmnt = (Element) fstNode;
							      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("vulnerability");
							      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
							      NodeList fstNm = fstNmElmnt.getChildNodes();
							      System.out.println("vulnerability : "  + ((Node) fstNm.item(0)).getNodeValue());
							      
							      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("keywords");
							      for(int l1 = 0; l1 < lstNmElmntLst.getLength(); l1++){
							    	  
							      System.out.println(lstNmElmntLst.getLength());
							 
							      Element lstNmElmnt = (Element) lstNmElmntLst.item(l1);
							      NodeList lstNm = lstNmElmnt.getChildNodes();
							      System.out.println("keywords : " + ((Node) lstNm.item(0)).getNodeValue());
							      String temp=((Node) lstNm.item(0)).getNodeValue();
							      System.out.println("value of temp is:"+temp);
							      int b=0;
							      if(s11.matches(((Node) lstNm.item(0)).getNodeValue())){
							    	  
							    	  System.out.println("time" +b);
							    	  temp1.add(((Node) fstNm.item(0)).getNodeValue());
							    	b++;
							
							    	  
							      }
							      
							      }
							    }

							  }
							
							
							
							
							
							
							int digits = s22.length();
							System.out.println("anuuuuuuuuuuuuuu");
							// System.out.println("ssssss"+setting+"ddddddd"+digits);
							if (digits >= setting) {
								System.out.println("hahahahahaha");
								if (res1.contains(s22)) {
									count++;
								}

							}
						}
						
						
						
												
						// System.out.println(count+"llllllllllllllllllllllllllll");
						if (count > 0) {
							R = "Yes";
						} else if (count == 0) {
							R = "";
						}

						if (url.contains(oo)) {
							System.out.println("errorrrr 2nd tym get request");
						} else {
							MainUrl.add(a);
							methodlist.add(method);
							MainUrl1.add(a);
							url.add(oo);
							for (org.apache.http.NameValuePair param1 : p1) {

								String s1 = param1.getName();
								String s2 = param1.getValue();

								if (l.contains(s1)) {
									int m = l.indexOf(s1);
									link[m].add(s1);
									// int aa=link[m].size();

								} else {
									// System.out.println("ddddddddddddddddddddddd**********************88aaaaaaaaaaaaaa");
									System.out.println("aneri" + d);
									link[d] = new LinkedList<String>();
									// System.out.println("hjklmnbv");
									link[d].add(s1);

									l.add(s1);
									// System.out.println("ddddddddddddddddddddddd**********************88aaaaaaaaaaaaaa");
									d++;
								}

							}

							for (int q = 0; q < l.size(); q++) {
								int bb = link[q].size();
								// System.out.println(l.size()+"bbbbbbbbbb"+bb);
								String ff = l.get(q).toString() + "(" + bb
										+ ")" + "\n";

								ll = ll.concat(ff);

							}
							q = ll.getBytes();
							map.setText(q);

						}

					}

					else {
						URL r = helpers.analyzeRequest(messageInfo).getUrl();
						String s = r.toString();
						// System.out.println("Inside"+s);
						String h = "";
						String t = "";
						String[] str;
						byte[] aa = messageInfo.getRequest();
						String bb = new String(aa, Charset.forName("UTF-8"));
						System.out.println("qqqqqqqqqqqqqqqqqqqqqqq" + bb);
						int len = bb.length();
						System.out.println(len);
						int a = bb.lastIndexOf("\n");
						// System.out.println(a);
						String cc = bb.substring(a + 1, len);
						str = cc.split("&|=");

						for (int i = 1; i < str.length; i = i + 2) {
							String f1 = str[i];
							if (res1.contains(f1)) {
								count++;
							}
						}
						if (count > 0) {
							R = "Yes";
						} else if (count == 0) {
							R = "";
						}

						
						for (int i = 0; i < str.length; i = i + 2) {
							String f = str[i];
							
							
							for (int l = 0; l < nodeLst.getLength(); l++) {

							    Node fstNode = nodeLst.item(l);
							    
							    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
							  
							           Element fstElmnt = (Element) fstNode;
							      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("vulnerability");
							      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
							      NodeList fstNm = fstNmElmnt.getChildNodes();
							      System.out.println("vulnerability : "  + ((Node) fstNm.item(0)).getNodeValue());
							      
							      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("keywords");
							      for(int l1 = 0; l1 < lstNmElmntLst.getLength(); l1++){
							    	  
							      System.out.println(lstNmElmntLst.getLength());
							 
							      Element lstNmElmnt = (Element) lstNmElmntLst.item(l1);
							      NodeList lstNm = lstNmElmnt.getChildNodes();
							      System.out.println("keywords : " + ((Node) lstNm.item(0)).getNodeValue());
							      String temp=((Node) lstNm.item(0)).getNodeValue();
							      if(f.matches(temp)){
							    	  System.out.println(f+"truee");
							    	  temp1.add(((Node) fstNm.item(0)).getNodeValue());
							    			
							    	   }
							      
							      }
							    }

							  }

						}	
						
						
						if (urlpost.contains(s)) {
							System.out.println("2nd tym post request");

						} else {
							MainUrl.add(s);
							methodlist.add(method);
							MainUrl1.add(cc);
							urlpost.add(s);

							// String[] temp;

							for (int i = 0; i < str.length; i = i + 2) {
								String f = str[i];
								
								
															
								
								if (l.contains(f)) {
									int m = l.indexOf(f);
									link[m].add(f);
								} else {
									// System.out.println("dhara"+d);
									link[d] = new LinkedList<String>();
									link[d].add(f);
									l.add(f);
									d++;
								}

							}

							for (int q = 0; q < l.size(); q++) {
								int b1 = link[q].size();

								String ff = l.get(q).toString() + "(" + b1
										+ ")" + "\n";

								// String kk=ff+"("+bb+")";
								ll = ll.concat(ff);

							}
							q = ll.getBytes();
							map.setText(q);
						}
					}
					
					for (int l1 = 0; l1 < nodeLst.getLength(); l1++) {
						System.out.println("temp1sizeee"+temp1.size());
					    Node fstNode = nodeLst.item(l1);
					    
					    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					  
					           Element fstElmnt = (Element) fstNode;
					      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("vulnerability");
					      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					      NodeList fstNm = fstNmElmnt.getChildNodes();
					      System.out.println("vulnerability : "  + ((Node) fstNm.item(0)).getNodeValue());
					      String vun= ((Node) fstNm.item(0)).getNodeValue();
					      if(temp1.contains(vun))
					      {
					    	  System.out.println("in temp1111111111"+temp1.get(l1));
					    	
					    	  x=x.concat(vun);
					      }
					
					    }
					}

					
					synchronized (log) {
						String p = "";
						
						int row = log.size();
						log.add(new LogEntry(toolFlag, helpers.analyzeRequest(
								messageInfo).getMethod(), callbacks
								.saveBuffersToTempFiles(messageInfo), helpers
								.analyzeRequest(messageInfo).getUrl(), helpers
								.analyzeRequest(messageInfo).getParameters(),
								nl, R, s, p,x));

						fireTableRowsInserted(row, row);
					}
					temp1.clear();
				}

				else {
					System.out.println("ooooooopsssss out of scope");
				}

			}

		}

		finally {

		}

	}

	//
	// extend AbstractTableModel
	//

	@Override
	public int getRowCount() {
		return log.size();
	}

	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Number";
		case 1:
			return "Method";
		case 2:
			return "URL";
		case 3:
			return "Reflection";
		case 4:
			return "Persistent";
		case 5:
			return "Vulnerability";
	

		default:
			return "";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LogEntry logEntry = log.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return logEntry.number;

		case 1:
			return logEntry.s;

		case 2:
			return logEntry.url.toString();

		case 3:
			return logEntry.v;

		case 4:
			return logEntry.p;
			
		case 5:
			return logEntry.x;
		
		default:
			return "";
		}
	}

	/*
	 * public void removeRow(int row){ fireTableRowsDeleted(row,row); }
	 */

	//
	// implement IMessageEditorController
	// this allows our request/response viewers to obtain details about the
	// messages being displayed
	//

	@Override
	public byte[] getRequest() {
		return currentlyDisplayedItem.getRequest();
	}

	@Override
	public byte[] getResponse() {
		return currentlyDisplayedItem.getResponse();
	}

	@Override
	public IHttpService getHttpService() {
		return currentlyDisplayedItem.getHttpService();
	}

	public String[] getWords() {

		String[] result = new String[wo.size()];
		for (int i = 0; i < wo.size(); i++) {
			result[i] = wo.get(i).toString();
			// System.out.println("resulttt"+result[i]);
		}

		return result;
	}

	//
	// extend JTable to handle cell selection
	//

	private class Table extends JTable {
		public Table(TableModel tableModel) {
			super(tableModel);
		}

		public void changeSelection(int row, int col, boolean toggle,
				boolean extend) {
			// show the log entry for the selected row

			LogEntry logEntry = log.get(row);
			System.out.println("rowwwwww" + row);
			// rv.setMessage(logEntry.requestResponse.getResponse(), false);
			byte[] bi = logEntry.bb;
			String res = new String(bi, Charset.forName("UTF-8"));
			// System.out.println("responseee"+res);

			String s = logEntry.url.toString();
			String y = logEntry.s;
			// System.out.println("*******"+y);
			byte[] b = null;
			byte[] b1 = null;
			byte[] b2 = null;
			byte[] b5 = null;
			byte[] b6 = null;
			String[] arr = new String[10];
			String[] arr1 = new String[10];
			String x = null;
			String z = "";
			String q2 = "";
		//	String p1 = "URL :";
			String p1="";
			String p2 = "";
			String p5 = "";
			String p6="";
			  NodeList nodeLst = null;
				try
				{
			//	 File file = new File(conf);
				  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				  DocumentBuilder db;
				
					db = dbf.newDocumentBuilder();
				  Document doc = db.parse(file1);
				  doc.getDocumentElement().normalize();
				  System.out.println("Root element " + doc.getDocumentElement().getNodeName());
				   nodeLst = doc.getElementsByTagName("category");
				  System.out.println("Information of all categories");
				}
					 catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					

			
			
			// String ea=logEntry.v;
			// System.out.println("######"+ea);
			if (y.equalsIgnoreCase("GET"))

			{

				List<org.apache.http.NameValuePair> params = null;
				try {
					params = URLEncodedUtils.parse(new URI(s), "UTF-8");
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String data = params.toString();
				for (org.apache.http.NameValuePair param : params) {
					// System.out.println(param);
					String s1 = param.getName();
					String s2 = param.getValue();
					
					
					for (int l = 0; l < nodeLst.getLength(); l++) {

					    Node fstNode = nodeLst.item(l);
					    
					    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					  
					           Element fstElmnt = (Element) fstNode;
					      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("vulnerability");
					      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					      NodeList fstNm = fstNmElmnt.getChildNodes();
					      System.out.println("vulnerability : "  + ((Node) fstNm.item(0)).getNodeValue());
					      
					      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("keywords");
					      for(int l1 = 0; l1 < lstNmElmntLst.getLength(); l1++){
					    	  
					      System.out.println(lstNmElmntLst.getLength());
					 
					      Element lstNmElmnt = (Element) lstNmElmntLst.item(l1);
					      NodeList lstNm = lstNmElmnt.getChildNodes();
					      System.out.println("keywords : " + ((Node) lstNm.item(0)).getNodeValue());
					      String temp=((Node) lstNm.item(0)).getNodeValue();
					      System.out.println("value of temp is:"+temp);
					   
					      if(s1.matches(((Node) lstNm.item(0)).getNodeValue())){
					    	  
					  	p6=p6+s1+"  :  "+((Node) fstNm.item(0)).getNodeValue()+"\n";
										    
								    	  
					      }
					      
					      }
					    }

					  }
					

					
					
					

			//		p2 = s1 + "\n";
					int count=0;
					for (int j = row + 1; j < log.size(); j++) {
						LogEntry l1 = log.get(j);
					
						byte[] rrr = l1.requestResponse.getResponse();
						String eee = new String(rrr, Charset.forName("UTF-8"));
						StringTokenizer st = new StringTokenizer(eee, " =\"<>/");
						while (st.hasMoreTokens()) {
							if (s2.equalsIgnoreCase(st.nextToken())) {
							
								if(count==0)
								{
									p2=s1+"\n";
								p1="URL :";
								p1 = p1 + ":" + l1.number;
								
								}
								else{
									p1 = p1 + ":" + l1.number;
								}
								count++;
								break;
							}
						}
					}
					String p3 = p2.concat(p1);
					String p4 = p3.concat("\n");
					p5 = p5.concat(p4);

					int digits = s2.length();
					// System.out.println("ssssss"+setting+"ddddddd"+digits);
					if (digits >= setting) {

						if (res.contains(s2)) {
							String q = s1;
							String q1 = q.concat("\n");
							q2 = q2.concat(q1);
						}
						String s3 = s1.concat("=");
						String s4 = s3.concat(s2);

						x = s4.concat("\n");
						z = z.concat(x);
					}
				}
				// System.out.println(z);
				String w = "GET" + "\n" + z;
				b = w.getBytes();
				b1 = data.getBytes();
				b2 = q2.getBytes();
				b5 = p5.getBytes();
				b6=p6.getBytes();
				requestViewer.setText(b);
				responseViewer.setText(b1);
				ref.setText(b2);
				persistent.setText(b5);
				xss.setText(b6);
			} else {
				String[] str;
				String[] str1;
				String temp = "";
				String h = "";
				String t = "";
				byte[] aa = logEntry.requestResponse.getRequest();
				String bb = new String(aa, Charset.forName("UTF-8"));
				// System.out.println(bb);

				int len = bb.length();
				System.out.println(len);
				int a = bb.lastIndexOf("\n");
				// System.out.println(a);
				String cc = bb.substring(a + 1, len);
				str1=cc.split("&|=");
				
				
				for (int i = 0; i < str1.length; i = i + 2) {
					String f = str1[i];
					
					
					for (int l = 0; l < nodeLst.getLength(); l++) {

					    Node fstNode = nodeLst.item(l);
					    
					    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					  
					           Element fstElmnt = (Element) fstNode;
					      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("vulnerability");
					      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					      NodeList fstNm = fstNmElmnt.getChildNodes();
					      System.out.println("vulnerability : "  + ((Node) fstNm.item(0)).getNodeValue());
					      
					      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("keywords");
					      for(int l1 = 0; l1 < lstNmElmntLst.getLength(); l1++){
					    	  
					      System.out.println(lstNmElmntLst.getLength());
					 
					      Element lstNmElmnt = (Element) lstNmElmntLst.item(l1);
					      NodeList lstNm = lstNmElmnt.getChildNodes();
					      System.out.println("keywords : " + ((Node) lstNm.item(0)).getNodeValue());
					//      String temp=((Node) lstNm.item(0)).getNodeValue();
					      if(f.matches(((Node) lstNm.item(0)).getNodeValue())){
					    	 	p6=p6+f+"  :  "+((Node) fstNm.item(0)).getNodeValue()+"\n";
					    			
					    	   }
					      
					      }
					    }

					  }

				}	

				
				
				

				byte ww[] = cc.getBytes(Charset.forName("UTF-8"));
				responseViewer.setText(ww);

				String del = "&";
				str = cc.split(del);
				for (int i = 0; i < str.length; i++) {
					temp = str[i];
					h = temp.concat("\n");
					t = t.concat(h);
				}
				String w = "POST" + "\n" + t;
				byte qq[] = w.getBytes(Charset.forName("UTF-8"));
				requestViewer.setText(qq);

				for (int i = 0; i < str.length; i++) {
					String te = str[i];
					String[] pv = te.split("=");
					String name;
					String value;
					// String name=pv[0];
					// String value=pv[1];

					if (pv.length == 1) {
						name = pv[0];
						value = "";
					} else {
						name = pv[0];
						value = pv[1];
					}

					if (!(value.equals(""))) {

			//			p2 = name + "\n";
						int count=0;
						for (int j = row + 1; j < log.size(); j++) {
							LogEntry l1 = log.get(j);
							byte[] rrr = l1.requestResponse.getResponse();
							String eee = new String(rrr,
									Charset.forName("UTF-8"));
							StringTokenizer st = new StringTokenizer(eee,
									" =\"<>/");
							while (st.hasMoreTokens()) {
								if (value.equalsIgnoreCase(st.nextToken())) {
									if(count==0)
									{
										p2=name+"\n";
									p1="URL :";
									p1 = p1 + ":" + l1.number;
								
									}
									else{
										p1 = p1 + ":" + l1.number;
									}
									count++;
									break;
								}
							}
						}
						String p3 = p2.concat(p1);
						String p4 = p3.concat("\n");
						p5 = p5.concat(p4);

						int number = value.length();

						if (number >= setting) {

							if (res.contains(value)) {
								String q = name;
								String q1 = q.concat("\n");
								q2 = q2.concat(q1);
							}

						}
					}
					b2 = q2.getBytes();
					b6=p6.getBytes();
					ref.setText(b2);
					b5 = p5.getBytes();
					persistent.setText(b5);
					xss.setText(b6);
				}

			}

			super.changeSelection(row, col, toggle, extend);
		}

	}

	// ;
	// class to hold details of each log entry
	//

	private static class LogEntry {
		final int tool;
		final IHttpRequestResponse requestResponse;
		byte[] bb;
		String s;
		String v;
		final URL url;
		List<IParameter> pe;
		int number;
		String p;
		String x;

		LogEntry(int tool, String s, IHttpRequestResponse requestResponse,
				URL url, List<IParameter> pe, byte[] cc, String v, int number,
				String p, String x) {
			this.tool = tool;
			this.requestResponse = requestResponse;
			this.s = s;
			this.v = v;
			this.url = url;
			this.pe = pe;
			this.bb = cc;
			this.number = number;
			this.p = p;
			this.x=x;

		}
	}

	class PopUpDemo extends JPopupMenu implements ActionListener {
		JMenuItem anItem;
		JMenuItem anItem1;

		public PopUpDemo() {
			anItem = new JMenuItem("Settings          ");
			anItem1 = new JMenuItem("Clear History                  ");
			add(anItem);
			add(anItem1);
			anItem.addActionListener(this);
			anItem1.addActionListener(this);

		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == anItem) {

				String message = JOptionPane.showInputDialog(null,
						"Enter no of characters");
				if (message != null) {
					setting = Integer.parseInt(message);
				}
			}

			if (e.getSource() == anItem1)

			{
				// System.out.println("In clear ");
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int response = JOptionPane.showConfirmDialog(null,
						"Are you sure You want to clear history", "Warning",
						dialogButton);
				if (response == JOptionPane.YES_OPTION) {
					int row = log.size();

					// System.out.println("In clear "+row);
					log.clear();
					fireTableRowsDeleted(0, 0);
					requestViewer.setText(null);
					responseViewer.setText(null);
					map.setText(null);
					persistent.setText(null);
					xss.setText(null);
					ref.setText(null);
					d = d - l.size();
					// System.out.println("in addd d is"+d);
					l.clear();
					url.clear();
					urlpost.clear();
					for (int j = 0; j < link.length; j++) {
						link[j].remove();
					}

				}
			}

		}

	}

	class PopClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		private void doPop(MouseEvent e) {
			PopUpDemo menu = new PopUpDemo();
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	class CustomActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == remove) {

				int b = combo.getSelectedIndex();
				Object o = combo.getSelectedItem();
				// System.out.println(b);
				combo.removeItemAt(b);
				scopeurl.remove(o);

				System.out.println(scopeurl);

			} else if (e.getSource() == per) {
				String w1;
				for (int i = 0; i < log.size(); i++) {

					LogEntry l = log.get(i);
					String w = l.requestResponse.getHost();

					if (w.equalsIgnoreCase("GET")) {
						w1 = l.requestResponse.getUrl().toString();
						List<org.apache.http.NameValuePair> params = null;
						try {
							params = URLEncodedUtils
									.parse(new URI(w1), "UTF-8");
						} catch (URISyntaxException ee) {
							// TODO Auto-generated catch block
							ee.printStackTrace();
						}

						for (org.apache.http.NameValuePair param : params) {

							String s1 = param.getName();
							String s2 = param.getValue();
							for (int j = i + 1; j < log.size(); j++) {
								LogEntry l1 = log.get(j);
								byte[] rrr = l1.requestResponse.getResponse();
								String eee = new String(rrr,
										Charset.forName("UTF-8"));
								StringTokenizer st = new StringTokenizer(eee,
										" =\"<>/");
								while (st.hasMoreTokens()) {
									if (s2.equalsIgnoreCase(st.nextToken())) {
										l.p = "Yes";
										fireTableRowsUpdated(i, i);

									}
								}
							}
						}
					} else {
						byte[] aa = l.requestResponse.getRequest();
						String bb = new String(aa, Charset.forName("UTF-8"));
						int len = bb.length();

						int a = bb.lastIndexOf("\n");
						String[] str;
						String s3 = "";
						String cc = bb.substring(a + 1, len);
						str = cc.split("&|=");
						for (int k = 1; k < str.length; k = k + 2) {
							s3 = str[k];
							for (int j = i + 1; j < log.size(); j++) {
								LogEntry l1 = log.get(j);
								byte[] rrr = l1.requestResponse.getResponse();
								String eee = new String(rrr,
										Charset.forName("UTF-8"));
								StringTokenizer st = new StringTokenizer(eee,
										" =\"<>/");
								while (st.hasMoreTokens()) {
									if (s3.equalsIgnoreCase(st.nextToken())) {
										l.p = "Yes";
										fireTableRowsUpdated(i, i);

									}
								}
							}

						}

					}

				}
			}

			else if (e.getSource() == analysis) {
				System.out.println("in analisisssssss" + l.size());

				wo.clear();

				for (int q = 0; q < l.size(); q++) {

					int b1 = link[q].size();
					// System.out.println("wprds = "+b1);

					for (int i = 0; i < b1; i++) {

						wo.add(l.get(q).toString());
						// System.out.println("value = == " + wo[c]);
						// c++;
					}

				}

				WordCloud.main(null);
			} else if (e.getSource() == add)

			{
				uu = text.getText();
				text.setText(null);
				combo.addItem(uu);
				scopeurl.add(uu);
			} 
		/*	else if(e.getSource()==config)
			{
			conf=	JOptionPane.showInputDialog(null, "Enter path of your xml file");
			System.out.println("fileeeeeeeee"+conf);
			}*/
			else if (e.getSource() == dump) {
				// Filechooser.main(null);
				File file2 = new File("C:/burplog");
				if (!file2.exists()) {
					if (file2.mkdir()) {
						System.out.println("Directory is created!");
					} else {
						System.out.println("Failed to create directory!");
					}

				}
				String eee = new String(q, Charset.forName("UTF-8"));

				try {
					String filename = "map" + new Date().getTime() + ".txt";
					File file1 = new File("C:/burplog/" + filename);

					// if file doesnt exists, then create it
					if (!file1.exists()) {
						file1.createNewFile();
					}

					FileWriter fw = new FileWriter(file1.getAbsoluteFile(),
							true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(eee);
					bw.close();

				} catch (IOException i) {

				}

				FileInputStream fis;
				try {
					fis = new FileInputStream("C:/Users/Public/my1.txt");
					// String fileName = new
					// SimpleDateFormat("dump.txt").format(new Date());
					String fileName = "dump" + new Date().getTime() + ".txt";
					System.out.println(fileName);
					FileOutputStream fos = new FileOutputStream("C:/burplog/"
							+ fileName);
					int ch;
					while ((ch = fis.read()) != -1) {
						char c = (char) ch;
						fos.write(c);

					}
					fis.close();
					fos.close();

				}

				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					String filename = "DetailMap" + new Date().getTime()
							+ ".txt";
					System.out.println("paraaaaaaaaaaa" + filename);
					File file3 = new File("C:/burplog/" + filename);

					// if file doesnt exists, then create it
					if (!file3.exists()) {
						file3.createNewFile();
					}

					FileWriter fw = new FileWriter(file3.getAbsoluteFile(),
							true);
					BufferedWriter bw = new BufferedWriter(fw);

					for (int i = 0; i < l.size(); i++) {

						String para = l.get(i).toString();
						int size1 = link[i].size();
						bw.write(para + "(" + size1 + ")" + ":" + "\n");
						System.out.println("sizeeee" + MainUrl1.size());
						for (int j = 0; j < MainUrl1.size(); j++) {
							String mo = methodlist.get(j).toString();
							String s1 = MainUrl1.get(j).toString();
							if (mo.equalsIgnoreCase("GET")) {

								List<org.apache.http.NameValuePair> p1 = null;
								try {
									p1 = URLEncodedUtils.parse(new URI(s1),
											"UTF-8");
									// System.out.println(p.toString());
								} catch (URISyntaxException e8) {
									// TODO Auto-generated catch block
									e8.printStackTrace();
								}

								for (org.apache.http.NameValuePair param1 : p1) {

									String s11 = param1.getName();
									String s22 = param1.getValue();
									if (para.equalsIgnoreCase(s11)) {

										bw.write(s1 + "\n");

									}

								}

							} else {
								System.out.println("postttttttt"+s1);
					
								String[] post = s1.split("&|=");
								for (int r = 0; r < post.length; r = r + 2) {

									String aa = post[r];
									String uu = MainUrl.get(j).toString();
									if (para.equalsIgnoreCase(aa)) {
										
										System.out.println("post url:"+uu);
										bw.write(uu + "\n");
									}
								}

							}
						}
						bw.write("\n");
					}
					bw.close();

				} catch (IOException k) {

				}
				JOptionPane.showMessageDialog(null,
						"Your files are saved successfully at C:/burplog ");
			}
			file.deleteOnExit();
		}

	}
}
