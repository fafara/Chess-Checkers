package blackop778.chess_checkers.graphics;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import blackop778.chess_checkers.Chess_Checkers;

public class Setup {
    public final Option game;
    public final Text whiteName;
    public final Text blackName;
    public final Option humans;
    public final Option black;
    public final Option internet;
    public final Option host;
    public final Text ip;
    public final Text port;
    public final JButton enter;
    private final JPanel panel;
    private final JDialog dialog;
    private boolean setup;
    private boolean buttonClosed;

    @SuppressWarnings("serial")
    public Setup() {
	setup = false;
	buttonClosed = false;
	dialog = new JDialog((JDialog) null);
	panel = new JPanel() {
	    // Ghetto solution to keep enter button on bottom
	    @Override
	    public Component add(Component comp) {
		if (setup) {
		    super.add(comp);
		    remove(enter);
		    super.add(enter);
		} else
		    super.add(comp);

		return comp;
	    }
	};
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	dialog.add(panel);
	game = new Option("Game:", "Chess", "Checkers", true, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		whiteName.setLabel("White's name:");
	    }

	}, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		whiteName.setLabel("Red's name:");
	    }
	});
	panel.add(game);
	whiteName = new Text("White's name:", 15, false);
	panel.add(whiteName);
	blackName = new Text("Black's name:", 15, false);
	panel.add(blackName);
	black = new Option("Play as black:", "Yes", "No", false) {
	    @Override
	    public void notified() {
		enter.setEnabled(true);
	    }
	};
	ip = new Text("IP address:", 15, true);
	port = new Text("Port number:", 4, true);
	host = new Option("Host the game:", "Yes", "No", true, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		panel.remove(ip);
		panel.repaint();
		dialog.pack();
	    }
	}, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		panel.add(ip);
		panel.repaint();
		dialog.pack();
	    }
	}, new Component[] { port }, new Component[] { ip, port });
	internet = new Option("Multiplayer over internet:", "Yes", "No", false, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		black.addCustom(panel, dialog);
		host.addCustom(panel, dialog);
		// humans.removeCustom(panel, dialog);
		whiteName.setLabel("Your name:");
		blackName.setLabel("Their name:");
	    }
	}, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		black.removeCustom(panel, dialog);
		host.removeCustom(panel, dialog);
		// humans.addCustom(panel, dialog);
		if (game.isButton1Selected()) {
		    whiteName.setLabel("White's name:");
		} else {
		    whiteName.setLabel("Red's name:");
		}
		blackName.setLabel("Black's name:");
	    }
	}, new Component[] { host }, new Component[0]) {
	    @Override
	    public void notified() {
		enter.setEnabled(bg.getSelection().getActionCommand().equals(label2));
	    }
	};
	humans = new Option("Number of humans:", "1", "2", false, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		internet.removeCustom(panel, dialog);
		black.addCustom(panel, dialog);
	    }
	}, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		black.removeCustom(panel, dialog);
		internet.addCustom(panel, dialog);
	    }
	}, new Component[] { black }, new Component[] { internet });
	humans.addCustom(panel, dialog);
	if (Chess_Checkers.DISABLE_AI) {
	    Enumeration<AbstractButton> buttons = humans.bg.getElements();
	    while (buttons.hasMoreElements()) {
		buttons.nextElement().setEnabled(false);
	    }
	}
	internet.addCustom(panel, dialog);
	if (Chess_Checkers.DISABLE_INTERNET) {
	    Enumeration<AbstractButton> buttons = internet.bg.getElements();
	    while (buttons.hasMoreElements()) {
		buttons.nextElement().setEnabled(false);
	    }
	}
	enter = new JButton("Enter");
	enter.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		buttonClosed = true;
		dialog.setVisible(false);
		// Playing local
		if (!internet.isButton1Selected()) {
		    if (whiteName.getText().equals("")) {
			if (game.isButton1Selected()) {
			    whiteName.setText("White");
			} else {
			    whiteName.setText("Red");
			}
		    }
		    if (blackName.getText().equals("")) {
			blackName.setText("Black");
		    }
		} else {
		    // Playing on internet
		    if (whiteName.getText().equals("")) {
			whiteName.setText("Your");
		    }
		    if (blackName.getText().equals("")) {
			blackName.setLabel("Your partner");
		    }
		}
	    }
	});
	panel.add(enter);
	setup = true;
	dialog.setTitle("Chess-Checkers Setup");
	dialog.pack();
	dialog.setResizable(false);
	dialog.setLocationRelativeTo(null);
	dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	dialog.setModal(true);
	dialog.setVisible(true);
    }

    public void redisplay() {
	buttonClosed = false;
	dialog.setVisible(true);
    }

    public boolean getButtonClosed() {
	return new Boolean(buttonClosed);
    }

    /**
     * Designed to be pretty generic but I decided to add some specifics instead
     * of having subclass hell in Setup
     */
    public class Option extends JPanel implements Notification {
	private static final long serialVersionUID = -1954244826051963946L;

	public final ButtonGroup bg;
	public final String label1;
	public final String label2;

	// When this is added and button1 is selected, these are added
	public final Component[] o1Next;
	// When this is added and button2 is selected, these are added
	public final Component[] o2Next;

	/**
	 * Adds no action listeners
	 * 
	 * @param label
	 * @param b1Label
	 * @param b2Label
	 * @param firstSelected
	 */
	public Option(String label, String label1, String label2, boolean firstSelected) {
	    this(label, label1, label2, firstSelected, null, null);
	}

	public Option(String label, String label1, String label2, boolean firstSelected, ActionListener al1,
		ActionListener al2) {
	    this(label, label1, label2, firstSelected, al1, al2, new Component[0], new Component[0]);
	}

	/**
	 * 
	 * @param label
	 * @param label1
	 * @param label2
	 * @param firstSelected
	 * @param al1
	 * @param al2
	 * @param o1Next
	 *            Don't make null, make empty
	 * @param o2Next
	 *            Don't make null, make empty
	 */
	public Option(String label, String label1, String label2, boolean firstSelected, ActionListener al1,
		ActionListener al2, Component[] o1Next, Component[] o2Next) {
	    this.label1 = label1;
	    this.label2 = label2;
	    this.o1Next = o1Next;
	    this.o2Next = o2Next;
	    if (o1Next == null)
		throw new NullPointerException("o1Next be null, will cause headaches");
	    if (o2Next == null)
		throw new NullPointerException("o2Next be null, will cause headaches");
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    add(new JLabel(label));
	    bg = new ButtonGroup();
	    JRadioButton b1 = new JRadioButton(label1, firstSelected);
	    b1.setActionCommand(label1);
	    if (al1 != null)
		b1.addActionListener(al1);
	    bg.add(b1);
	    add(b1);
	    JRadioButton b2 = new JRadioButton(label2, !firstSelected);
	    b2.setActionCommand(label2);
	    if (al2 != null)
		b2.addActionListener(al2);
	    bg.add(b2);
	    add(b2);
	}

	/**
	 * Allows for additional actions to be performed when added to a
	 * Container
	 * 
	 * @param container
	 */
	public void addCustom(Container container, Window window) {
	    container.add(this);
	    Component[] a;
	    if (bg.getSelection().getActionCommand().equals(label1))
		a = o1Next;
	    else
		a = o2Next;
	    for (Component c : a) {
		if (c instanceof Option) {
		    Option o = (Option) c;
		    o.addCustom(container, window);
		} else
		    container.add(c);
	    }
	    if (setup) {
		Component c = container.getComponents()[container.getComponentCount() - 1];
		if (c instanceof Notification) {
		    Notification n = (Notification) c;
		    n.notified();
		}
	    }
	    container.repaint();
	    window.pack();
	}

	/**
	 * Allows for additional actions to be performed when removed from a
	 * Container
	 * 
	 * @param container
	 */
	public void removeCustom(Container container, Window window) {
	    container.remove(this);
	    Component[] a;
	    if (bg.getSelection().getActionCommand().equals(label1))
		a = o1Next;
	    else
		a = o2Next;
	    for (Component c : a) {
		if (c instanceof Option) {
		    Option o = (Option) c;
		    o.removeCustom(container, window);
		} else
		    container.remove(c);
	    }
	    if (setup) {
		Component c = container.getComponents()[container.getComponentCount() - 1];
		if (c instanceof Notification) {
		    Notification n = (Notification) c;
		    n.notified();
		}
	    }
	    container.repaint();
	    window.pack();
	}

	@Override
	public void notified() {
	    enter.setEnabled(false);
	}

	public boolean isButton1Selected() {
	    return bg.getSelection().getActionCommand().equals(label1);
	}
    }

    public class Text extends JPanel implements Notification {
	private static final long serialVersionUID = -1712072660292711164L;
	public final JTextField text;
	private final JLabel label;

	public Text(String labelText, int textFieldWidth, boolean important) {
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    add(label = new JLabel(labelText));
	    text = new JTextField(textFieldWidth);
	    text.setEditable(true);
	    if (important) {
		text.getDocument().addDocumentListener(new DocumentListener() {

		    @Override
		    public void changedUpdate(DocumentEvent arg0) {
			enter.setEnabled(!text.getText().isEmpty());
		    }

		    @Override
		    public void insertUpdate(DocumentEvent arg0) {
			enter.setEnabled(!text.getText().isEmpty());
		    }

		    @Override
		    public void removeUpdate(DocumentEvent arg0) {
			enter.setEnabled(!text.getText().isEmpty());
		    }

		});
	    }
	    add(text);
	}

	public void setLabel(String text) {
	    label.setText(text);
	}

	@Override
	public void notified() {
	    enter.setEnabled(!text.getText().isEmpty());
	}

	public String getText() {
	    return text.getText();
	}

	public void setText(String newText) {
	    text.setText(newText);
	}
    }

    public interface Notification {
	public abstract void notified();
    }
}
