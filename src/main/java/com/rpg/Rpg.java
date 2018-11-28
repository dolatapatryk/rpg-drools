package com.rpg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class Rpg {

	public static void main(String[] args) {
		try {
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");
        	KieRuntimeLogger kLogger = ks.getLoggers().newFileLogger(kSession, "test");

            
            RpgUI ui = new RpgUI(new StartCallback(kSession));
            ui.createAndShowGUI();
        } catch (Throwable t) {
            t.printStackTrace();
        }
	}
	
	public static class RpgUI extends JPanel {
		private JTextArea output;
		private StartCallback startCallback;
		
		public RpgUI(StartCallback startCallback) {
			super(new BorderLayout());
			
			this.startCallback = startCallback;
			
            JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
            add( splitPane,
                 BorderLayout.CENTER );

            //create top half of split panel and add to parent
            JPanel topHalf = new JPanel();
            topHalf.setLayout( new BoxLayout( topHalf,
                                              BoxLayout.X_AXIS ) );
            topHalf.setBorder( BorderFactory.createEmptyBorder( 5,
                                                                5,
                                                                0,
                                                                5 ) );
            topHalf.setMinimumSize( new Dimension( 400,
                                                   50 ) );
            topHalf.setPreferredSize( new Dimension( 450,
                                                     250 ) );
            splitPane.add( topHalf );

            //create bottom top half of split panel and add to parent
            JPanel bottomHalf = new JPanel( new BorderLayout() );
            bottomHalf.setMinimumSize( new Dimension( 400,
                                                      50 ) );
            bottomHalf.setPreferredSize( new Dimension( 450,
                                                        300 ) );
            splitPane.add( bottomHalf );

            //Container that list container that shows available store items
            JPanel listContainer = new JPanel( new GridLayout( 1,
                                                               1 ) );
            listContainer.setBorder( BorderFactory.createTitledBorder( "Options" ) );
            topHalf.add( listContainer );


            JPanel tableContainer = new JPanel( new GridLayout( 1,
                                                                1 ) );
            tableContainer.setBorder( BorderFactory.createTitledBorder( "Table" ) );
            topHalf.add( tableContainer );


            //Create panel for checkout button and add to bottomHalf parent
            JPanel checkoutPane = new JPanel();
            JButton button = new JButton( "Start" );
            button.setVerticalTextPosition( AbstractButton.CENTER );
            button.setHorizontalTextPosition( AbstractButton.LEADING );
            //attach handler to assert items into working memory
            button.addMouseListener( new StartButtonHandler() );
            button.setActionCommand( "checkout" );
            checkoutPane.add( button );
            bottomHalf.add( checkoutPane,
                            BorderLayout.NORTH );

            button = new JButton( "Reset" );
            button.setVerticalTextPosition( AbstractButton.CENTER );
            button.setHorizontalTextPosition( AbstractButton.TRAILING );
            //attach handler to assert items into working memory
            button.setActionCommand( "reset" );
            checkoutPane.add( button );
            bottomHalf.add( checkoutPane,
                            BorderLayout.NORTH );

            //Create output area, imbed in scroll area an add to bottomHalf parent
            //Scope is at instance level so it can be easily referenced from other
            // methods
            output = new JTextArea( 1,
                                    10 );
            output.setEditable( false );
            JScrollPane outputPane = new JScrollPane( output,
                                                      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            bottomHalf.add( outputPane,
                            BorderLayout.CENTER );
            output.append("tutaj chyba bysmy mogli wypisywać pytanie\n");
            output.append("tam gdzie jest \"List\" mozna chyba wyswietlac dostepne opcje do pytania\n");
            
            this.startCallback.setTextArea(output);
		}
		
		public void createAndShowGUI() {
			JFrame frame = new JFrame("RPG");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			setOpaque(true);
			frame.setContentPane(this);
			
			frame.pack();
			frame.setVisible(true);
		}
		
		private class StartButtonHandler extends MouseAdapter {
			public void mouseReleased(MouseEvent e) {
				JButton button = (JButton) e.getComponent();
				startCallback.checkout((JFrame) button.getTopLevelAncestor());
			}
		}
	}
	
	public static class StartCallback {
		KieSession kSession;
        JTextArea textArea;

        public StartCallback(KieSession kSession) {
            this.kSession = kSession;
        }

        public void setTextArea(JTextArea textArea) {
            this.textArea = textArea;
        }

        
        public void checkout(JFrame frame) {


            //add the JFrame to the ApplicationData to allow for user interaction
        	kSession.setGlobal("frame", frame);
        	kSession.setGlobal("textArea", this.textArea);
            kSession.fireAllRules();
        }
    }

}

