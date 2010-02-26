package oo2cad.cad.logic;

import java.util.Vector;

import oo2cad.cad.objects.CadBaseObject;
import oo2cad.cad.objects.ObjectBox;
import oo2cad.config.Config;
import oo2cad.shapes.Shape;

/**
 * BasisKlasse f�r alle Cad-Aktionen. Diese Klasse wird von
 * OO2CAD.java aufgerufen.
 * @author ahrensm
 *
 */
public class CadHandler {
	
	private Vector<Shape> shapeList;
	private Vector<CadBaseObject> cadObjectList;
	private Config config;

	public CadHandler(Config config) {
		this.config = config;
	}

	
	public void createCadCode(Vector<Shape> shapeList) {
		
		//Konvertierung der Koordinaten von OpenOffice (Nullpunkt links oben) zu CAD (Nullpunkt links unten)
		//new ZeroPointConverter().convertValues(shapeList);
		
		/*
		 * ShapeObjecte auslesen und x,y Min,Max bestimmen f�r ObjectBox
		 */
		
		ObjectBoxValueGetter obvg = new ObjectBoxValueGetter(shapeList);
		obvg.objectboxValuesMaxMin();
		
		//ObjetBox mit Werten bef�llen
		ObjectBox objectBox = new ObjectBox(obvg.getxMin(), obvg.getxMax(), obvg.getyMin(), obvg.getyMax(), null);
		
		//Hier werden die einzelnen Shapes von der fixen Lage geloest
		//und relativ zum Bezugspunkt angegeben
		CoordinateConverter coco = new CoordinateConverter(Config.getInstance());
		coco.convertToRelative(objectBox.getxMin(), objectBox.getyMin(), shapeList);
		
		//Mithilfe des CADConverters werden die Shape-Objekte in die
		//fuer CAD-Code ben�tigten Linien und Boegen umgewandelt
		CadConverter cadConverter = new CadConverter();
		cadObjectList = cadConverter.convertShapes(shapeList);
		
		//cadListe der Objektbox hinzufuegen
		objectBox.setCadObjectList(cadObjectList);
		//Offset zu objectbox hinzurechnen
		objectBox.setxMax(objectBox.getxMax() + config.getOffSetX());
		objectBox.setxMin(objectBox.getxMin() + config.getOffSetX());
		objectBox.setyMax(objectBox.getyMax() + config.getOffSetY());
		objectBox.setyMin(objectBox.getyMin() + config.getOffSetY());
		
		CadCreator cadCreator = new CadCreator(objectBox);
		cadCreator.createCADFile("h:\\cad.vec");
					
	}

	public Vector<Shape> getShapeList() {
		return shapeList;
	}
}
