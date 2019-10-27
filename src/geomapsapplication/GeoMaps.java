/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geomapsapplication;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.geotools.data.CachingFeatureSource;
import org.geotools.data.FeatureSource;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapContext;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.control.JMapStatusBar;
import org.geotools.swing.data.JFileDataStoreChooser;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataUtilities;
import static org.geotools.data.Parameter.CRS;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.RasterLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.*;

import org.geotools.swing.action.InfoAction;
import org.geotools.swing.action.MapAction;
import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Srinivas
 */
public class GeoMaps {

    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
    JFrame frame_mainFrame = new JFrame();
    static GeoMaps obj_geomaps = new GeoMaps();
    static JMapPane eastPane;
    JLabel lbl_status;
    JSplitPane spp_p;
    JPanel panel_mainPanel;
    boolean pointTool = true;
    boolean pointClicked = false;
    int pointOption = 0;
    boolean lineClicked = false;
    boolean rectClicked = false;
    boolean polyClicked = false;
    static JButton btn_point;
    int pointFlag = 0;
    DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
    DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
    public static Vector<Object> ColumnNames = new Vector();
    Vector<Vector<Object>> distanceVector = new Vector<>();
    Vector<Coordinate> coordinateVector = new Vector<>();
//    static latLongSimulator obj_simulator;
    static boolean movePoint = false;
    Layer pLayer = new FeatureLayer(pointCollection, null);
    Layer Llayer = new FeatureLayer(lineCollection, null);
    //Coordinate[] ls = new Coordinate[30];
    static MapContext map = new MapContext();
    public static Timer timer;
    double moveLatitude;
    double moveLongitude;

    public static void main(String[] args) throws Exception {
        ColumnNames.add("Distance From");
        ColumnNames.add("Distance");
        ColumnNames.add("Bearing");
        obj_geomaps.createMainFrame();

    }

    private JPanel createMainPanel() throws Exception {

        panel_mainPanel = new JPanel();
        panel_mainPanel.setLayout(new BorderLayout(4, 4));
//        panel_mainPanel.add(createWestPanel(), BorderLayout.WEST);
//        panel_mainPanel.add(createEastPanel(), BorderLayout.EAST);
        spp_p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(createWestPanel()), new JScrollPane(createEastPanel()));
        spp_p.setDividerLocation(70);
        panel_mainPanel.add(spp_p, BorderLayout.CENTER);
        panel_mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
        panel_mainPanel.add(createstatusbar(), BorderLayout.SOUTH);
        return panel_mainPanel;
    }

    private JPanel createstatusbar() {
        JPanel statusbar = new JPanel();
        statusbar.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
        lbl_status = new JLabel("Status");
        statusbar.setSize(new Dimension(600, 800));
        statusbar.add(lbl_status);
        return statusbar;
    }

    private JToolBar createNorthPanel() {
        JToolBar toolbar_LOS = new JToolBar();
        toolbar_LOS.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
        toolbar_LOS.setBackground((new java.awt.Color(204, 104, 155)));
        toolbar_LOS.setFloatable(false);

        JButton btn_mouse = new JButton(new NoToolAction(eastPane));
        toolbar_LOS.add(btn_mouse);
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_mouse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pointTool = true;
            }
        });

        JButton btn_zoomin = new JButton(new ZoomInAction(eastPane));
        toolbar_LOS.add(btn_zoomin);
        btn_zoomin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Zoom in Tool Selected");
                pointTool = false;
                pointClicked = false;
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));

        JButton btn_zoomout = new JButton(new ZoomOutAction(eastPane));
        toolbar_LOS.add(btn_zoomout);
        btn_zoomout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Zoom out Tool Selected");
                pointTool = false;
                pointClicked = false;
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));

        JButton btn_pan = new JButton(new PanAction(eastPane));
        toolbar_LOS.add(btn_pan);
        btn_pan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Pane Tool Selected");
                pointTool = false;
                pointClicked = false;
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        JButton btn_infoAction = new JButton(new InfoAction(eastPane));
        toolbar_LOS.add(btn_infoAction);
        btn_infoAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Info Tool Selected");
                pointTool = false;
                pointClicked = false;
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));

        JButton btn_resetAction = new JButton(new ResetAction(eastPane));
        toolbar_LOS.add(btn_resetAction);
        btn_resetAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Reset Tool Selected");
                pointTool = false;
                pointClicked = false;
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));
//        JButton btn_loadSHP = new JButton();
//        btn_loadSHP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/loadSHPico.png")));
//        toolbar_LOS.add(btn_loadSHP);
//        toolbar_LOS.addSeparator(new Dimension(3, 0));
//        btn_loadSHP.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JLabel confirmmsg = new JLabel("Current Map will be Cleared... Are You Sure?");
//                int i = JOptionPane.showConfirmDialog(null, confirmmsg, "New Map will be Loaded..!", JOptionPane.OK_CANCEL_OPTION);
//                if (i == JOptionPane.OK_OPTION) {
//                    loadSHPmap();
//                }
//                
//            }
//        });
//        
//        JButton btn_loadTIF = new JButton();
//        btn_loadTIF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/loadTIFico.png")));
//        toolbar_LOS.add(btn_loadTIF);
//        toolbar_LOS.addSeparator(new Dimension(3, 0));
//        btn_loadTIF.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JLabel confirmmsg = new JLabel("Current Map will be Cleared... Are You Sure?");
//                int i = JOptionPane.showConfirmDialog(null, confirmmsg, "New Map will be Loaded..!", JOptionPane.OK_CANCEL_OPTION);
//                if (i == JOptionPane.OK_OPTION) {
//                    loadTIFmap();
//                }
//                
//            }
//        });
        btn_point = new JButton();
        btn_point.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/pointico.png")));
        toolbar_LOS.add(btn_point);
        btn_point.setToolTipText("Draw a Point");
        btn_point.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Point Tool Selected");

                if (!pointTool) {
                    lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                    lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 1;
                }
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));

        JButton btn_pointText = new JButton();
        btn_pointText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/pointText.png")));
        toolbar_LOS.add(btn_pointText);
        btn_pointText.setToolTipText("Draw a Point with Text");
        btn_pointText.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Point with Text Tool Selected");

                if (!pointTool) {
                    lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                    lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 2;
                }
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));

        JButton btn_img = new JButton();
        btn_img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/logo.png")));
        toolbar_LOS.add(btn_img);
        btn_img.setToolTipText("Draw a Point with Image icon");
        btn_img.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Image Tool Selected");

                if (!pointTool) {
                    lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                    lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 3;
                }
            }
        });
        toolbar_LOS.addSeparator(new Dimension(3, 0));

        JButton btn_line = new JButton();
        btn_line.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/lineico.png")));
        toolbar_LOS.add(btn_line);
        btn_line.setToolTipText("Draw a Line using Points");
        btn_line.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                lbl_status.setText("Point Tool Selected");
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

        toolbar_LOS.addSeparator(new Dimension(3, 0));
        JButton btn_rect = new JButton();
        btn_rect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/polygonico.png")));
        toolbar_LOS.add(btn_rect);
        btn_rect.setToolTipText("Draw Polygon using points");
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_rect.addActionListener(new ActionListener() {
            //  
            @Override
            public void actionPerformed(ActionEvent e) {
                lbl_status.setText("Point Tool Selected");
                for (int d = 0; d < pointFlag; d++) {
                    Coordinate[] tempCoordinates = new Coordinate[2];
                    tempCoordinates[0] = coordinateVector.elementAt(d);
                    if (d + 1 != pointFlag) {
                        tempCoordinates[1] = coordinateVector.elementAt(d + 1);
                    } else {
                        tempCoordinates[1] = coordinateVector.elementAt(0);
                    }
                    try {
                        //Style style = SLD.createLineStyle(Color.BLUE, 1);
//                        System.out.println("distance between points:" + );
                        getLayerLineByCoord(d, tempCoordinates);

                    } catch (SchemaException ex) {
                        Logger.getLogger(GeoMaps.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

//        JButton btn_polygon = new JButton();
//        btn_polygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/polygonico.png")));
//        toolbar_LOS.add(btn_polygon);
//        toolbar_LOS.addSeparator(new Dimension(3, 0));
//        btn_polygon.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                polyClicked = true;
//            }
//        });
        JButton btn_loadTif = new JButton();
        btn_loadTif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/loadTIFico.png")));
        btn_loadTif.setToolTipText("Load a TIF Map");
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_loadTif.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                loadTIFmap();
            }
        });
        toolbar_LOS.add(btn_loadTif);
        JButton btn_loadSHP = new JButton();
        btn_loadSHP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/loadSHPico.png")));
        btn_loadSHP.setToolTipText("Load a SHP Map");
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_loadSHP.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                loadSHPmap();
            }
        });
        toolbar_LOS.add(btn_loadSHP);

        JButton btn_distance = new JButton("Distance");
        // btn_distance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/polygonico.png")));
        btn_distance.setToolTipText("Distance between points");
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_distance.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showDistanceWindow();
            }
        });
        toolbar_LOS.add(btn_distance);

        JButton btn_move = new JButton("Start Moving");
        // btn_distance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/polygonico.png")));
        btn_move.setToolTipText("Distance between points");
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_move.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                movePoint = true;
                pointClicked = false;
                lbl_status.setText("Select One point");
                pointOption = 1;
            }
        });
        toolbar_LOS.add(btn_move);

        JButton btn_StopMove = new JButton("Stop Moving");
        // btn_distance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/polygonico.png")));
        btn_StopMove.setToolTipText("Distance between points");
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_StopMove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                movePoint = false;
                if (timer.isRunning()) {
                    timer.stop();
                }
            }
        });
        toolbar_LOS.add(btn_StopMove);

        JButton btn_deleteLayer = new JButton("Delete");
        btn_deleteLayer.setToolTipText("Distance between points");
        toolbar_LOS.addSeparator(new Dimension(3, 0));
        btn_deleteLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (map.layers().size() > 1) {
                    map.removeLayer(map.layers().size() - 1);
                }
            }

        });
        toolbar_LOS.add(btn_deleteLayer);
        return toolbar_LOS;
    }

    private void loadTIFmap() {
//        try {
//            System.out.println("in Tif fun");
//            File file = JFileDataStoreChooser.showOpenFile("tif", null);
//            if (file == null) {
//               JLabel msg = new JLabel("No File Selected..!");
//            Object o[] = {msg};
//            JOptionPane.showMessageDialog(null, o, "File Selection", 1);
//            }else{

//        AbstractGridFormat format = GridFormatFinder.findFormat(file);
//        AbstractGridCoverage2DReader reader = format.getReader(file);
//        Layer rasterLayer = new GridReaderLayer(reader, getTechStyle());
//        MapContext map = new DefaultMapContext();
//        map.setTitle("LineofSight");
        System.out.println("in Tif fun");
        File file = JFileDataStoreChooser.showOpenFile("tif", null);
        if (file == null) {
            return;
        }

        try {
            GeoTiffReader reader = new GeoTiffReader(file);
            RasterLayer rasterLayer = new GridReaderLayer(reader, getTechStyle());
            rasterLayer.setTitle(file.getName());
            map = new DefaultMapContext();
            map.setTitle("LineofSight");
            map.addLayer(rasterLayer);
            try {
                obj_geomaps.createMainFrame();
            } catch (Exception ex) {
                Logger.getLogger(GeoMaps.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(GeoMaps.class.getName()).log(Level.SEVERE, null, ex);
        }

                   //Pane.getMapContent().addLayer(rasterLayer);
    }

    private void loadSHPmap() {

        try {
            File file = JFileDataStoreChooser.showOpenFile("shp", null);
            if (file == null) {
                JLabel msg = new JLabel("No File Selected..!");
                Object o[] = {msg};
                JOptionPane.showMessageDialog(null, o, "File Selection", 1);
            } else {
                FileDataStore store = FileDataStoreFinder.getDataStore(file);
                SimpleFeatureSource featureSource = store.getFeatureSource();
                // Create a map content and add our shapefile to it
                MapContent map = new MapContent();
                // map.setTitle("Quickstart");
                Style style = SLD.createSimpleStyle(featureSource.getSchema());
                Layer layer = new FeatureLayer(featureSource, style);
                map.addLayer(layer);
                panel_mainPanel.setLayout(new BorderLayout(4, 4));
                eastPane = new JMapPane(map);
                spp_p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(createWestPanel()), new JScrollPane(eastPane));
                spp_p.setDividerLocation(100);
                panel_mainPanel.add(spp_p, BorderLayout.CENTER);
                panel_mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
                panel_mainPanel.add(createstatusbar(), BorderLayout.SOUTH);

            }
        } catch (Exception e) {
            System.out.println("excep:" + e);
        }
    }

    private JMapPane createEastPanel() throws Exception {   
        //File file = JFileDataStoreChooser.showOpenFile("tif", null);
        File file = new File("F:\\56k.tif");
        if (file == null) {
            JLabel msg = new JLabel("No File Selected..!");
            Object o[] = {msg};
            JOptionPane.showMessageDialog(null, o, "File Selection", 1);
        } else {

            GeoTiffReader reader = new GeoTiffReader(file);
            RasterLayer rasterLayer = new GridReaderLayer(reader, getTechStyle());
            rasterLayer.setTitle(file.getName());
            //Pane.getMapContent().addLayer(rasterLayer);

//        AbstractGridFormat format = GridFormatFinder.findFormat(file);
//        AbstractGridCoverage2DReader reader = format.getReader(file);
//        //    Layer rasterLayer = createg
//        Layer layer = new GridReaderLayer(reader, defaultStyle);
            map = new DefaultMapContext();
            map.setTitle("LineofSight");
            map.addLayer(rasterLayer);

//        Layer pLayer = addPoint(78.27, 17.3623);
//        Layer p2Layer = addPoint(78.11, 17.3623);
//        map.addLayer(pLayer);
//        map.addLayer(p2Layer);
            // LineString lstr = new LineString(null, null);
            eastPane = new JMapPane(map);
            eastPane.addMouseListener(new MapMouseAdapter() {
                public void onMouseClicked(MapMouseEvent ev) {
                    DirectPosition2D pos = ev.getWorldPos();
                     System.out.println("latitude" + pos.x + "longitude" + pos.y);
                    lbl_status.setText("latitude: " + pos.x + " longitude: " + pos.y);
                    if (pointClicked) {
                        addPoint(pos.x, pos.y);
                    }
                    if (movePoint) {
                        lbl_status.setText("latitude: " + pos.x + " longitude: " + pos.y);
                        movePoint = false;
                        movePointFun(pos.x, pos.y);
                    // addPoint(pos.x, pos.y);
                        // obj_simulator.generateLatLong(pos.x, pos.y);

                    }

//                    
//                    Style style1 = fun();
//                    Layer pLayer1 = new FeatureLayer(pointCollection, style1);
//                    map.addLayer(pLayer1);
                    //else if (lineClicked) {
//
//                    pointFlag++;
//                    if (pointFlag == 1) {
//                        Coordinate cr;
//                        lbl_status.setText("Select First point");
//                        Layer pointLayer1 = addPoint(pos.x, pos.y);
//                        cr = new Coordinate(pos.x, pos.y);
//                        ls[0] = cr;
//                        map.addLayer(pointLayer1);
//
//                    } else if (pointFlag == 2) {
//                        try {
//                            lbl_status.setText("Select Second point");
//                            Layer pointLayer2 = addPoint(pos.x, pos.y);
//                            Coordinate cr2 = new Coordinate(pos.x, pos.y);
//                            map.addLayer(pointLayer2);
//                            ls[1] = cr2;
//                            Layer layer;
//                            layer = getLayerLineByCoord(ls);
//                            map.addLayer(layer);
//
//                        } catch (SchemaException ex) {
//                            Logger.getLogger(GeoMaps.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                    if (pointFlag == 3) {
//                        Layer l = new Layer() {
//
//                            @Override
//                            public ReferencedEnvelope getBounds() {
//                                return null;
//                            }
//                        };
//                        map.addLayer(l);
//                        map.removeLayer(l);
//                        lineClicked = false;
//                        pointFlag = 0;
//                    }
//                }
                }

                @Override
                public void onMouseEntered(MapMouseEvent ev) {
//            System.out.println("Mouse Entered");
                    lbl_status.setText("Mouse Entered");
                }

                @Override
                public void onMouseExited(MapMouseEvent ev) {
                    //displayNoCursor();
                }

                @Override
                public void onMouseMoved(MapMouseEvent ev) {
                    //  System.out.println("pos" + ev.getWorldPos());
                    lbl_status.setText("position: " + ev.getWorldPos());
                }

            });
        }
        return eastPane;

    }

    private JToolBar createWestPanel() {
        JToolBar toolbar_westPanel = new JToolBar();
        toolbar_westPanel.setLayout(new BoxLayout(toolbar_westPanel, BoxLayout.Y_AXIS));
        toolbar_westPanel.setBackground((new java.awt.Color(104, 104, 155)));
        toolbar_westPanel.setFloatable(false);

        JButton btn_BELsystem = new JButton();
//        btn_BELsystem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/logoText.png")));
        btn_BELsystem.setToolTipText("Place BEL system on the Map");
        // btn_BELsystem.setBorderPainted(true);
//        btn_BELsystem.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 204)), javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(51, 0, 255))));
//        btn_BELsystem.setContentAreaFilled(false);
//        btn_BELsystem.setFocusPainted(false);
//        btn_BELsystem.setOpaque(false);
        btn_BELsystem.setBorder(new EmptyBorder(5, 5, 5, 5));
//        btn_BELsystem.setForeground(Color.red);
//        btn_BELsystem.setBackground(new java.awt.Color(56, 158, 240));
        btn_BELsystem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                //Image image = toolkit.getImage("icons/handwriting.gif");

                Cursor c = toolkit.createCustomCursor(toolkit.getImage(getClass().getResource("/geomaps/icons/logo.png")), new java.awt.Point(0, 0), "img");
                eastPane.setCursor(c);
                lbl_status.setText("Image Tool Selected");

                if (!pointTool) {
                    lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                    lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 4;
                }

            }
        });

        toolbar_westPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        toolbar_westPanel.add(btn_BELsystem);
        toolbar_westPanel.add(Box.createRigidArea(new Dimension(10, 15)));
        JButton btn_radarvan = new JButton();
//        btn_radarvan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/radarVan.jpg")));
        btn_radarvan.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                //Image image = toolkit.getImage("icons/handwriting.gif");

                Cursor c = toolkit.createCustomCursor(toolkit.getImage(getClass().getResource("/geomaps/icons/radarVan.jpg")), new java.awt.Point(0, 0), "img");
                eastPane.setCursor(c);
                lbl_status.setText("Image Tool Selected");

                if (!pointTool) {
                    lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                    lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 5;
                }
            }
        });
        toolbar_westPanel.add(btn_radarvan);
        toolbar_westPanel.add(Box.createRigidArea(new Dimension(10, 15)));
        JButton btn_van = new JButton();
        btn_van.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/van.jpg")));
        toolbar_westPanel.add(btn_van);
        btn_van.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                //Image image = toolkit.getImage("icons/handwriting.gif");

                Cursor c = toolkit.createCustomCursor(toolkit.getImage(getClass().getResource("/geomaps/icons/van.jpg")), new java.awt.Point(0, 0), "img");
                eastPane.setCursor(c);
                lbl_status.setText("Image Tool Selected");

                if (!pointTool) {
                    lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                    lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 6;
                }
            }
        });
        toolbar_westPanel.add(Box.createRigidArea(new Dimension(10, 15)));
        JButton btn_vehicle = new JButton();
        btn_vehicle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geomaps/icons/vehicle.jpg")));
        btn_vehicle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                //Image image = toolkit.getImage("icons/handwriting.gif");

                Cursor c = toolkit.createCustomCursor(toolkit.getImage(getClass().getResource("/geomaps/icons/vehicle.jpg")), new java.awt.Point(0, 0), "img");
                eastPane.setCursor(c);
                lbl_status.setText("Image Tool Selected");

                if (!pointTool) {
                    lbl_status.setText("Select Point Tool First");
                    JOptionPane.showConfirmDialog(null, "Select Mouse Pointer tool first..", "Select Pointer Tool", JOptionPane.OK_CANCEL_OPTION);
                } else {
                    lbl_status.setText("Select One point");
                    pointClicked = true;
                    pointOption = 7;
                }
            }
        });
        toolbar_westPanel.add(btn_vehicle);

        return toolbar_westPanel;
    }

    /**
     * @param args the command line arguments
     */
    public JFrame createMainFrame() throws Exception {
        frame_mainFrame.setTitle("Training Simulator");
        frame_mainFrame.setLocation(0, 0);
        frame_mainFrame.setSize(1280, 980);
        frame_mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_mainFrame.setContentPane(createMainPanel());
        frame_mainFrame.setVisible(true);
        return frame_mainFrame;
    }

    public static Style getTechStyle() {
        StyleFactory sf = CommonFactoryFinder.getStyleFactory();
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        Style techstyle = SLD.wrapSymbolizers(sym);
        return techstyle;
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
            SimpleFeature feature = featureBuilder.buildFeature("SS");
            pointCollection.add(feature);
            Style style = createPointStyle(pointOption);
            map.removeLayer(pLayer);
            pLayer = new FeatureLayer(pointCollection, style);
            map.addLayer(pLayer);
            System.out.println("coutn" + map.layers().size());
            pointFlag++;
            // return lineCollection;
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

    public void getLayerLineByCoord(int d, Coordinate[] coords) throws SchemaException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        LineString line = geometryFactory.createLineString(coords);
        SimpleFeatureType TYPE = DataUtilities.createType("test", "line", "the_geom:LineString");
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder((SimpleFeatureType) TYPE);
        featureBuilder.add(line);
        SimpleFeature feature = featureBuilder.buildFeature("LineString_Sample");
//        System.out.println("distance:" + coords[0].x + coords[0].y + "seco" + coords[1].x + coords[1].y);
//        System.out.println("distance:" + distFrom(coords[0].x, coords[0].y, coords[1].x, coords[1].y));
        distFrom(d, coords[0].x, coords[0].y, coords[1].x, coords[1].y);
        lineCollection = new DefaultFeatureCollection();
        lineCollection.add(feature);
        //  return lineCollection;
        Style style = SLD.createLineStyle(Color.BLUE, 1);
        //map.removeLayer(Llayer);
        Llayer = new FeatureLayer(lineCollection, style);
        map.addLayer(Llayer);
    }

    public Style createPointStyle(int pointOpt) {
        StyleBuilder sb = new StyleBuilder();

        Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark = styleFactory.getSquareMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.CYAN), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(5));
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);
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
            symb.getOptions().put("maxDisplacement", "150");
            // Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symb);
        }
        if (pointOpt == 4 || pointOpt == 5 || pointOpt == 6 || pointOpt == 7) {
            eastPane.setCursor(Cursor.getDefaultCursor());
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

    public void movePointFun(double lat, double lon) {
        moveLatitude = lat;
        moveLongitude = lon;
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addPoint(moveLatitude, moveLongitude);
                moveLatitude = (moveLatitude + 0.00876543213123);
                moveLongitude = (moveLongitude + 0.00876543213124);
                System.out.println("move lat" + moveLatitude + moveLongitude);
            }
        });
        timer.start();
    }

    public Style function() {
        StyleBuilder sb = new StyleBuilder();
        TextSymbolizer textSymbolizer = sb.createTextSymbolizer(sb.createFill(Color.BLACK),
                new Font[]{sb.createFont("Lucida Sans", 10),
                    sb.createFont("Arial", 10)},
                sb.createHalo(), sb.attributeExpression("name"), null, null);
        Mark circle = sb.createMark(StyleBuilder.MARK_SQUARE, Color.GREEN);
        Graphic graph2 = sb.createGraphic(null, circle, null, 1, 4, 0);
        PointSymbolizer pointSymbolizer = sb.createPointSymbolizer(graph2);
        FeatureTypeStyle fts = sb.createFeatureTypeStyle("labelPointFeature",
                new Symbolizer[]{textSymbolizer, pointSymbolizer});
        Style style = styleFactory.createStyle();
        style.addFeatureTypeStyle(fts);
        return style;

    }

    public void fun() {

//        List<SimpleFeature> list = new ArrayList<SimpleFeature>();
//    SimpleFeature feature = build.buildFeature("fid1", new Object[]{ geom.point(707009.4375,243649.18750000006), "TestImage" } );
//    list.add( feature );
//    SimpleFeatureCollection collection = new ListFeatureCollection(cantonSchema, list);
//    FeatureSource source = new CollectionFeatureSource(collection);
//
//    StyleBuilder sb = new StyleBuilder();
//    Graphic graphic = sb.createGraphic();
//    URL url = getClass().getClassLoader().getResource("img/Explosion.png");
//    ExternalGraphic external = sb.createExternalGraphic( url, "image/png");
//    graphic.graphicalSymbols().clear();
//    graphic.graphicalSymbols().add( external );
//    graphic.setSize(filterFactory.literal(30));
//
//
//    PointSymbolizer pointSymbolizer = styleFactory.createPointSymbolizer(graphic, null);
//    pointSymbolizer.getOptions().put("maxDisplacement", "150");
//    Rule rule = styleFactory.createRule();
//    rule.symbolizers().add(pointSymbolizer);
//    // tie this rule to the featureId
//    rule.setFilter(ff2.id(feature.getIdentifier()));
//
//    Style singleEventStyle = SLD.wrapSymbolizers(pointSymbolizer);
//
//
//    Layer singeEventLayer = new FeatureLayer(source, singleEventStyle);
//    map.addLayer(singeEventLayer);
    }

    private Style createLineStyle() {
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLUE),
                filterFactory.literal(1));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    //public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
//        double earthRadius = 6371000; //meters
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLng = Math.toRadians(lng2 - lng1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        float dist = (float) (earthRadius * c);
//
//        return dist;
    //  }
    private void distFrom(int d, double lat1, double lng1, double lat2, double lng2) {
        double y = Math.sin(lng2 - lng1) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        if (brng > 0) {
            brng = 180 + (180 - brng);
        } else {
            brng = brng * -1;
        }
        String bearing = Double.toString(brng);
        int dotIndex = bearing.indexOf('.');
        bearing = bearing.substring(0, dotIndex + 3);
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (double) (earthRadius * c);
        String distance = Double.toString(dist);
        dotIndex = distance.indexOf('.');
        distance = distance.substring(0, dotIndex + 3);
        Vector<Object> tempVect = new Vector<>();
        tempVect.add("From" + (d + 1) + "to" + (d + 2));
        tempVect.add(distance + " Meters");
        tempVect.add(bearing + " Degrees");

        if (distanceVector.size() < coordinateVector.size() - 1) {
            System.out.println("distance:" + distanceVector.size() + " ls " + coordinateVector.size());
            distanceVector.add(tempVect);
        }

        // return distance;
    }

    private void showDistanceWindow() {
        JScrollPane distanceSP = new JScrollPane();
        JTable distanceTable = new JTable(distanceVector, ColumnNames);
//                DefaultTableModel model = new DefaultTableModel(passiveTrkVector, ColumnNames);
//                distanceTable.setModel(model);
        distanceSP.setPreferredSize(new Dimension(600, 400));
        distanceSP.setViewportView(distanceTable);
        distanceTable.setShowHorizontalLines(false);
        distanceTable.setShowVerticalLines(false);
        distanceTable.setOpaque(false);
        distanceTable.setRowHeight(22);
        distanceTable.setRowMargin(5);
        distanceTable.setAlignmentX(12);
        distanceTable.setBackground(Color.BLACK);
        distanceTable.setAutoCreateRowSorter(true);
        distanceTable.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        distanceTable.setForeground(Color.WHITE);
        distanceSP.getViewport().setBackground(Color.BLACK);
        if (distanceVector.size() > 0) {
            Object o[] = {distanceSP};
            JOptionPane.showMessageDialog(null, o, "Distance and Bearing", 1);
        } else {

            JLabel msg = new JLabel("No Data Found");
            Object o[] = {msg};
            JOptionPane.showMessageDialog(null, o, "Distance and Bearing", 1);
        }
    }
}
