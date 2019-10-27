/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geomapsapplication;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import static geomapsapplication.GeoMaps.btn_point;
import static geomapsapplication.GeoMaps.eastPane;
import static geomapsapplication.GeoMaps.map;
import java.awt.BorderLayout;
import java.awt.Color;
import static java.awt.Color.RED;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.Format;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Pane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContext;
import org.geotools.map.RasterLayer;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import static org.geotools.styling.SLD.defaultStyle;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.TextSymbolizer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.coordinate.LineString;

/**
 *
 * @author BEL_Samyukta
 */
public class GeoMapsApplication {
    
    JFrame frame_mainFrame;
    JPanel panel_mainPanel;
    JPanel panel_northpanel;
    JPanel panel_southpanel;
    JPanel panel_westpanel;
    JMapPane panel_centerpanel;
    MapContext map;
    //JLabel lbl_status;
    boolean pointTool = true;
    boolean pointClicked = false;
    int pointOption = 0;
    int pointFlag = 0;
    JButton btn_point;//= new JButton("POINT");;
    JButton btn_line;
    DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
    Layer Llayer = new FeatureLayer(lineCollection, null);
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
    Fill fill = styleFactory.fill(null, filterFactory.literal(Color.RED), filterFactory.literal(4.0));
    DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
    Vector<Coordinate> coordinateVector = new Vector<>();
    Layer pLayer = new FeatureLayer(pointCollection, null);

    /**
     * @param args the command line arguments
     */
    public void createMainFrame(){
        frame_mainFrame =  new JFrame();
        frame_mainFrame.setSize(new Dimension(1300, 900));
        //frame_mainFrame.setLocation(100, 300);
        //frame_mainFrame.setLayout(new BorderLayout(5, 5));
        frame_mainFrame.setContentPane(createMainPanel());
        frame_mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_mainFrame.show();
    }
    
    private JPanel createMainPanel(){
        panel_mainPanel = new JPanel();
        panel_mainPanel.setLayout(new BorderLayout(5, 5));
        panel_mainPanel.add(createCenterPanel(),BorderLayout.CENTER);
        panel_mainPanel.add(createNorthPanel(),BorderLayout.NORTH);
        panel_mainPanel.add(createSouthPanel(),BorderLayout.SOUTH);
        panel_mainPanel.add(createWestPanel(),BorderLayout.WEST);
        return panel_mainPanel;
    }
    private JMapPane createCenterPanel(){
         //panel_centerpanel = new JPanel();
         //panel_centerpanel.setBackground(Color.yellow);
          File file = new File("C:\\Users\\Anusha\\Desktop\\landsat.tif");
          //File file = JFileDataStoreChooser.showOpenFile("tif", null);
          if (file == null)
           {
            JLabel msg = new JLabel("No File Selected..!");
            Object o[] = {msg};
            JOptionPane.showMessageDialog(null, o, "File Selection", 2);
            } 
          else
          {
            try{
                GeoTiffReader reader = new GeoTiffReader(file);
            RasterLayer rasterLayer = new GridReaderLayer(reader, getTechStyle());
            rasterLayer.setTitle(file.getName());
            
                //Pane.getMapContent.addLayer(rasterLayer);

//    AbstractGridFormat format = GridFormatFinder.findFormat(file);
       // AbstractGridCoverage2DReader reader1 = Format.getReader(file);
         //   Layer rasterLayer = createg
     //  Layer layer = new GridReaderLayer(reader, defaultStyle(null));
            map = new DefaultMapContext();
            map.setTitle("LineofSight");
            map.addLayer(rasterLayer);

   // Layer pLayer1=new GridReaderLayer(reader, null);
 // addPoint(78.27, 17.3623);
//        Layer p2Layer = addPoint(78.11, 17.3623);
     // map.addLayer(pLayer);
//        map.addLayer(p2Layer);
//                LineString lstr = new LineString(null, null);
            panel_centerpanel = new JMapPane(map);
        //eastPane = new JMapPane(map);
            // addPoint(78.27, 17.3623);
            panel_centerpanel.addMouseListener(new MapMouseAdapter() {
                public void onMouseClicked(MapMouseEvent ev) {
                    DirectPosition2D pos = ev.getWorldPos();
                   //  System.out.println("latitude" + pos.x + "longitude" + pos.y);
                   // lbl_status.setText("latitude: " + pos.x + " longitude: " + pos.y);
                    if (pointClicked) {
                        addPoint(pos.x, pos.y);
                      //  addPoint(78.27, 17.3623);
                    }
                   /* if (movePoint) {
                        lbl_status.setText("latitude: " + pos.x + " longitude: " + pos.y);
                        movePoint = false;
                        movePointFun(pos.x, pos.y);
                    // addPoint(pos.x, pos.y);
                        // obj_simulator.generateLatLong(pos.x, pos.y);
*/
                  //  });
            
            //}catch(Exception e){
               // System.out.println(e);
                }         // }
                     });
            }
        catch(Exception e){
               System.out.println(e);
        }
        
        
        
        }
    return panel_centerpanel;
        }
    public static Style getTechStyle() {
        StyleFactory sf = CommonFactoryFinder.getStyleFactory();
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        Style techstyle = SLD.wrapSymbolizers(sym);
        return techstyle;
    }
    private JPanel createNorthPanel(){
        panel_northpanel = new JPanel();
        panel_northpanel.setBackground(Color.BLACK);
        panel_northpanel.setLayout(new FlowLayout(0, 5, 5));
        
        JButton btn_mouse = new JButton(new NoToolAction(panel_centerpanel));
//        btn_mouse.setText("cursor");
//        btn_mouse.setIcon(null);
        JButton btn_zoomin = new JButton(new ZoomInAction(panel_centerpanel));
        JButton btn_zoomout = new JButton(new ZoomOutAction(panel_centerpanel));
        JButton btn_pan = new JButton(new PanAction(panel_centerpanel));
        JButton btn_resize = new JButton(new ResetAction(panel_centerpanel));
        panel_northpanel.add(btn_mouse);
        panel_northpanel.add(btn_zoomin);
        panel_northpanel.add(btn_zoomout);
        panel_northpanel.add(btn_pan);
        panel_northpanel.add(btn_resize);
       // panel_northpanel.add(btn_point);
                     btn_point = new JButton("POINT");
                     
       // btn_point.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/pointico.png")));
        //toolbar_LOS.add(btn_point);
        btn_point.setToolTipText("Draw a Point");
        btn_point.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // lbl_status.setText("Point Tool Selected");

                if (!pointTool) {
                   // lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                   // lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 1;
                }
            }
        });
      //  btn_line.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/lineico.png")));
       // toolbar_LOS.add(btn_line);
        btn_line = new JButton("LINE");
        btn_line.setToolTipText("Draw a Line using Points");
        btn_line.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
             pointClicked = false;
               // lbl_status.setText("Point Tool Selected");
                for (int d = 0; d < pointFlag; d++) {
                    Coordinate[] tempCoordinates = new Coordinate[2];
                    tempCoordinates[0] = coordinateVector.elementAt(d);
                    if (d + 1 != pointFlag) {
                        tempCoordinates[1] = coordinateVector.elementAt(d + 1);
                    } else {
                        break;
                    }

                    try {

                        //Style style = SLD.createLineStyle(Color.BLUE, 1);
                        getLayerLineByCoord(d, tempCoordinates);
                    } catch (SchemaException ex) {
                        Logger.getLogger(GeoMaps.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
//                     showDistanceWindow();
//                if (!pointTool) {
//                    lbl_status.setText("Select Point Tool First");
//                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
//                } else {
//                    lbl_status.setText("Select One point");
//                    lineClicked = true;
//                    ls = new Coordinate[2];
//                }
            }

        });
panel_northpanel.add(btn_line);
        panel_northpanel.add(btn_point);
        return panel_northpanel;
    }
    public void getLayerLineByCoord(int d, Coordinate[] coords) throws SchemaException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
      com.vividsolutions.jts.geom.LineString line = geometryFactory.createLineString(coords);
        SimpleFeatureType TYPE = DataUtilities.createType("test", "line", "the_geom:LineString");
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder((SimpleFeatureType) TYPE);
        featureBuilder.add(line);
        SimpleFeature feature = featureBuilder.buildFeature("LineString_Sample");
//        System.out.println("distance:" + coords[0].x + coords[0].y + "seco" + coords[1].x + coords[1].y);
//        System.out.println("distance:" + distFrom(coords[0].x, coords[0].y, coords[1].x, coords[1].y));
       // distFrom(d, coords[0].x, coords[0].y, coords[1].x, coords[1].y);
        lineCollection = new DefaultFeatureCollection();
        lineCollection.add(feature);
        //  return lineCollection;
        Style style = SLD.createLineStyle(Color.BLUE, 10);
        //map.removeLayer(Llayer);
        Llayer = new FeatureLayer(lineCollection, style);
        map.addLayer(Llayer);
    }
    private JPanel createSouthPanel(){
        panel_southpanel = new JPanel();
        panel_southpanel.setBackground(Color.BLUE);
        return panel_southpanel;
    }
    private JPanel createWestPanel(){
        panel_westpanel = new JPanel();
        panel_westpanel.setBackground(Color.GREEN);
        return panel_westpanel;
    }
    public void addPoint(double latitude, double longitude) {
        System.out.println("lat and long in point" + latitude + "   " + longitude);
        System.out.println("\n");
        try {
//            SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
//            
//            b.setName("MyFeatureType");
//            b.setCRS(DefaultGeographicCRS.WGS84);
//            b.add("location", Point.class);
            // final SimpleFeatureType TYPE = b.buildFeatureType();
            final SimpleFeatureType TYPE = DataUtilities.createType("Location",
                    "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
                    "name:String," + // <- a String attribute
                    "number:Integer" // a number attribute
            );
             SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
            Coordinate cr;
            cr = new Coordinate(latitude, longitude);
            coordinateVector.add(cr);

            featureBuilder.add(point);
            if (pointOption == 2 || pointOption == 4) {
                String featureName = JOptionPane.showInputDialog(null, "Enter Name of Place");
                featureBuilder.add(featureName);
            } else {
                featureBuilder.add("");
            }
            featureBuilder.add(pointFlag);
            SimpleFeature feature = featureBuilder.buildFeature(String.valueOf(pointFlag));
            pointCollection.add(feature);
            Style style = createPointStyle(pointOption);
            map.removeLayer(pLayer);
            pLayer = new FeatureLayer(pointCollection, style);
            map.addLayer(pLayer);
            System.out.println("count" + map.layers().size());
            pointFlag++;
            //return lineCollection;
            // DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);
//        featureCollection.add(feature);
//        Style style = createPointStyle();
//
//        Layer layer = new FeatureLayer(featureCollection, style);
//        return layer;
        } catch (SchemaException ex) {
            Logger.getLogger(GeoMaps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     public Style createPointStyle(int pointOpt) {
        StyleBuilder sb = new StyleBuilder();

        Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.GREEN), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.red)));
        
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10));
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr,null);
        Rule rule = styleFactory.createRule();
        if (pointOpt == 1 || pointOpt == 2) {
            rule.symbolizers().add(sym);
        }
       if (pointOpt == 2 || pointOpt == 5 || pointOpt == 4 || pointOpt == 6 || pointOpt == 7) {
          TextSymbolizer ts = sb.createTextSymbolizer(sb.createFill(Color.BLACK), new Font[]{sb.createFont("Lucida Sans", 13),
               sb.createFont("Arial", 13)}, sb.createHalo(), sb.attributeExpression("name"), null, null);
           rule.symbolizers().add(ts);
       }

        // StyleBuilder sb = new StyleBuilder();
        // Graphic graphic = sb.createGraphic();
        //URL url = getClass().getClassLoader().getResource("img/Explosion.png");
        if (pointOpt == 3 || pointOpt == 4 || pointOpt == 5 || pointOpt == 6 || pointOpt == 7) {

            Graphic grahic = styleFactory.createDefaultGraphic();
            PointSymbolizer symb = styleFactory.createPointSymbolizer(grahic, null);
            ExternalGraphic external = sb.createExternalGraphic(getClass().getResource("/geomaps/icons/logo.png"), "image/png");;
            if (pointOpt == 4) {
                external = sb.createExternalGraphic(getClass().getResource("/geomaps/icons/logo.png"), "image/png");
            }
            if (pointOpt == 5) {
                external = sb.createExternalGraphic(getClass().getResource("/geomaps/icons/radarVan.jpg"), "image/png");
            }
            if (pointOpt == 6) {
                external = sb.createExternalGraphic(getClass().getResource("/geomaps/icons/van.jpg"), "image/png");
            }
            if (pointOpt == 7) {
                external = sb.createExternalGraphic(getClass().getResource("/geomaps/icons/vehicle.jpg"), "image/png");
            }

            //grahic.graphicalSymbols().clear();
            grahic.graphicalSymbols().add(external);
            grahic.setSize(filterFactory.literal(20));
            // PointSymbolizer pointSymbolizer = styleFactory.createPointSymbolizer(graphic, null);
            //symb.getOptions().put("maxDisplacement", "150");
            // Rule rule = styleFactory.createRule();
            //rule.symbolizers().add(symb);
        }
        if (pointOpt == 4 || pointOpt == 5 || pointOpt == 6 || pointOpt == 7) {
            panel_centerpanel.setCursor(Cursor.getDefaultCursor());
            pointClicked = false;
            pointOption = 0;

        }
        // tie this rule to the featureId
//    rule.setFilter(ff2.id(feature.getIdentifier()));
        //Style singleEventStyle = SLD.wrapSymbolizers(pointSymbolizer);
        //Layer singeEventLayer = new FeatureLayer(source, singleEventStyle);
        // map.addLayer(singeEventLayer);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style s = sb.createStyle();
        s.featureTypeStyles().add(fts);
        return s;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        GeoMapsApplication gma = new GeoMapsApplication();
        gma.createMainFrame();
    }
    
    
    
}
