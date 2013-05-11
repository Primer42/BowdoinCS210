/**
 * Will Richard
 * 9/20/07
 * Lab 2
 * A program that allows to user to draw various shapes, in several different
 * colors and configurations.  Also includes a function to clear the screen and 
 * remove the last shape placed.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.ArrayList;


public class DrawingWillR extends JFrame implements MouseInputListener
{

    private JMenuBar menuBar;   //menu bar for all tools and options
    
    //declare buttons for shapes
    private JRadioButtonMenuItem rectangle = new JRadioButtonMenuItem("Rectangle");
    private JRadioButtonMenuItem roundRect = new JRadioButtonMenuItem("Round-Cornered Rectangle");
    private JRadioButtonMenuItem line = new JRadioButtonMenuItem("Line");
    private JRadioButtonMenuItem ellipse = new JRadioButtonMenuItem("Ellipse");
    
    //declare buttons for tools
    private JMenuItem clear = new JMenuItem("Clear Canvas");
    private JMenuItem undo = new JMenuItem("Woops! Undo");
    
    //declare menu items for colors
    private JMenuItem penColor = new JMenuItem("Border Color");
    private JMenuItem backColor = new JMenuItem("Background Color");
    private JMenuItem fillColor = new JMenuItem("Fill Color");
    
    //decare various other necessary variables
    private Graphics g;
    private Shape currShape;
    private Color pen= Color.black;
    private Color background = Color.gray;
    private Color fill = null;
    
    private Point2D.Double startPoint = new Point2D.Double(0,0);    //point to record where mouse was pressed
    private Point2D.Double endPoint = new Point2D.Double(0,0);      //point to record where shape boundries are
    
    //ArrayLists to store all information regarding previous drawns shapes
    private ArrayList<Shape> previousShapes = new ArrayList<Shape>();
    private ArrayList<Point2D.Double> previousStarts = new ArrayList<Point2D.Double>();
    private ArrayList<Point2D.Double> previousEnds = new ArrayList<Point2D.Double>();
    private ArrayList<Color> previousBorders = new ArrayList<Color>();
    private ArrayList<Color> previousFills = new ArrayList<Color>();
    
    //constructor
    public DrawingWillR(){
        super("Lab 2- Drawing; Will Richard");
        setSize(500,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        
        menuBar = new JMenuBar();   //declaring menu bar for rest of menu items

        //set up shape menu drop down and options
       JMenu shapes = new JMenu("Shapes");
       shapes.setMnemonic(KeyEvent.VK_A);
       
       //making shape choices radio buttons and setting them up
       rectangle.setSelected(true);
       rectangle.setMnemonic(KeyEvent.VK_R);
       roundRect.setMnemonic(KeyEvent.VK_R);
       line.setMnemonic(KeyEvent.VK_R);
       ellipse.setMnemonic(KeyEvent.VK_R);

       shapes.add(rectangle);
       shapes.add(roundRect);
       shapes.add(line);
       shapes.add(ellipse);

       ButtonGroup shapeButtons = new ButtonGroup();
       shapeButtons.add(rectangle);
       shapeButtons.add(roundRect);
       shapeButtons.add(line);
       shapeButtons.add(ellipse);

       //adding action listeners for shapes- each one sets currShape to the shape selected
       rectangle.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                currShape = new Rectangle2D.Double();
            }
        });
        
        roundRect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currShape = new RoundRectangle2D.Double();
            }
        });
        
        
        line.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                currShape = new Line2D.Double();
            }
        });
        
        
        ellipse.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                currShape = new Ellipse2D.Double();
            }
        });
               
        //setting up tools menu, with clear and undo functions
       JMenu tools = new JMenu("Tools");
       tools.setMnemonic(KeyEvent.VK_A);
       tools.add(undo);
       tools.add(clear);
       
       //clear window of all drawings and all previously drawn shapes
       clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                update(g);
                previousShapes.clear();
                previousStarts.clear();
                previousEnds.clear();
                previousBorders.clear();
                previousFills.clear();
            }
        });
        
        //undo button calls undo method- for more details, see the method itself
        undo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                undo();
            }
        });
       
        //setting up color menu and sub menus
       JMenu color = new JMenu("Color");
       color.setMnemonic(KeyEvent.VK_A);
       
       color.add(penColor);
       color.add(backColor);
       color.add(fillColor);
       
       //uses JColorChooser's showDialog() method to bring up a dialoge to choose colors for each color variable.
        penColor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pen = JColorChooser.showDialog(
                        DrawingWillR.this,
                        "Choose Border Color, or press Cancel to have no border color",
                        pen);
           }
        });
       
        backColor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                background = JColorChooser.showDialog(
                        DrawingWillR.this,
                        "Choose a Background Color",
                        background);
                 setBackground(background);
            }
        });
            
        fillColor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                fill = JColorChooser.showDialog(
                        DrawingWillR.this,
                        "Choose a Fill Color, or press Cancel to have no Fill Color",
                        fill);
             }
        });
                
        //adding each individual topic menu to menu bar
        menuBar.add(shapes);
        menuBar.add(tools);
        menuBar.add(color);
        
        this.setJMenuBar(menuBar);  //setting the menu bar in the window
        
        setBackground(background);  //setting the background to the default background color
        
        setVisible(true);
    }
    
    //implementing MouseInputListener Methods
        
    //When mouse is pressed, record where it is pressed
    public void mousePressed(MouseEvent e){
        startPoint = new Point2D.Double(e.getX(), e.getY());
    }
    
    //when mouse button is released, this method records where this occured
    //then sets up ArrayLists to record all necessary information about the shape about to be drawn
    //then draws the shape
    public void mouseReleased(MouseEvent e){
        endPoint = new Point2D.Double(e.getX(), e.getY());
        previousShapes.add(currShape);
        previousEnds.add(new Point2D.Double(e.getX(), e.getY()));
        previousStarts.add(startPoint);
        previousBorders.add(pen);
        previousFills.add(fill);
        paint(startPoint, endPoint, currShape, pen, fill, false);
    }
    
    //while mouse is being dragged, this method erases where the previous shape was
    //then it draws a shape to the current mouse position
    //allows user to see what they are drawing before deciding where to put it
    public void mouseDragged(MouseEvent e){
        if(endPoint == null){
            endPoint = startPoint;
        }
        paint(startPoint, endPoint, currShape, pen, fill, true);
        endPoint.setLocation(e.getX(), e.getY());
        redraw();
        paint(startPoint, endPoint, currShape, pen, fill, false);
    }

    //these methods of MouseInputListener not needed
    public void mouseMoved(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
        
    //this method finally draws the shape in question.  Also is able to erase previous shapes, depending on the state of the boolean erase
    private void paint(Point2D.Double pStartPoint, Point2D.Double pEndPoint, Shape shape, Color penColor, Color fillColor, boolean erase) {
        //these temp points are to help get rid of problems with negative widths and heigths later in the method
        Point2D.Double tempStartPoint = null;
        Point2D.Double tempEndPoint = null;
        
        //this getsGraphics on Graphics object g, then castes it to a Graphics2D object to take advantage of the Graphics2D methods
        g = getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        
        //this allows for the user to start drawing immediately without having to select a shape from the menu
        if(shape == null){
            shape = new Rectangle2D.Double();
        }
        
        //this is where negative widths and heights are accounted for
        if(shape instanceof RectangularShape) {
            if(pEndPoint.getX()-pStartPoint.getX() < 0){                                    //if the width is going to be negative
                double temp = pStartPoint.getX();                                           //make the temp start and end points positioned 
                tempStartPoint = new Point2D.Double(pEndPoint.getX(), pStartPoint.getY());  //in such a way that the width will be positive
                tempEndPoint = new Point2D.Double(temp, pEndPoint.getY());
            }
            else{                                                                           //otherwise, just leave them as is,
                tempStartPoint = pStartPoint;                                               //and assign them to tempStartPoint
                tempEndPoint = pEndPoint;
            }
            
            if(pEndPoint.getY()-pStartPoint.getY() <0){                                     //this does the same thing as the previous if statements
                double temp = pStartPoint.getY();                                           //but to the width
                tempStartPoint = new Point2D.Double(tempStartPoint.getX(), pEndPoint.getY());
                tempEndPoint = new Point2D.Double(tempEndPoint.getX(), temp);
            }
        }
        
        //these next if statements set up shape to have the correct dimentions, since each shape has a different method to do this
        if(shape instanceof Rectangle2D.Double){
            ((Rectangle2D.Double) shape).setRect(tempStartPoint.getX(), tempStartPoint.getY(), tempEndPoint.getX()-tempStartPoint.getX(), tempEndPoint.getY()-tempStartPoint.getY() );
        }
        
        //the last two doubles passed to setFrame here allow the arc height and width of the rounded rectangle's corners to be based upon the size of the shape, rather than an arbitrary constant
        if(shape instanceof RoundRectangle2D.Double){
            ((RoundRectangle2D.Double) shape).setRoundRect(tempStartPoint.getX(), tempStartPoint.getY(), tempEndPoint.getX()-tempStartPoint.getX(), tempEndPoint.getY()-tempStartPoint.getY(), ((tempEndPoint.getY()-tempStartPoint.getY()) * .30), ((tempEndPoint.getX()-tempStartPoint.getX()) * .30) );
        }
        
        if(shape instanceof Ellipse2D.Double){
            ((Ellipse2D.Double) shape).setFrame(tempStartPoint.getX(), tempStartPoint.getY(), tempEndPoint.getX()-tempStartPoint.getX(), tempEndPoint.getY()-tempStartPoint.getY() );
        }
            
        if(shape instanceof Line2D.Double){
            ((Line2D.Double) shape).setLine(pStartPoint.getX(), pStartPoint.getY(), pEndPoint.getX(), pEndPoint.getY() );
        }
        
        //this finally draws the shapes, changing various colors depending on whether this to erase a shape, or if there is a fill or border selected
        if(erase){
            g.setColor(background);
            g2d.draw(shape);
            g2d.fill(shape);
            return;
        }
       
        if (fillColor != null){
                g.setColor(fillColor);
                g2d.fill(shape);
        }
        
        if (penColor != null){
            g.setColor(penColor);
            g2d.draw(shape);
        }
    }

    //after erasing a shape, its "shadow" is left- this redraws all the previous shapes to remove that shadow and refreshs the window and the menu bar
    private void redraw(){
        for(int i = 0; i < previousShapes.size();i++){
            paint(previousStarts.get(i), previousEnds.get(i), previousShapes.get(i), previousBorders.get(i), previousFills.get(i), false);
        }
        menuBar.updateUI();
    }
    
    //this undos the last shape by erasing the last shape stored in the ArrayLists, then removing those entries into the ArrayLists
    private void undo(){
        int i = previousShapes.size()-1;
        paint(previousStarts.get(i), previousEnds.get(i), previousShapes.get(i), previousBorders.get(i), previousFills.get(i), true);
        previousStarts.remove(i);
        previousEnds.remove(i);
        previousShapes.remove(i);
        previousBorders.remove(i);
        previousFills.remove(i);
        redraw();
    }
    
    //a main method to make starting the class easier
    public static void main (String[] args){
    	DrawingWillR draw = new DrawingWillR();
    }
}