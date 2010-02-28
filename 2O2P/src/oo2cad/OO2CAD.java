package oo2cad;

import java.io.File;
import java.util.Vector;

import oo2cad.cad.logic.CadHandler;
import oo2cad.config.Config;
import oo2cad.exception.OO2CADException;
import oo2cad.shapes.Shape;
import oo2cad.unzip.Unzip;
import oo2cad.xml.OOXMLParser;

import org.apache.log4j.Logger;

public class OO2CAD {

	private static Logger log = Logger.getLogger(OO2CAD.class);
	
	/**
	 * Initiale Klasse um das Programm zu starten.
	 */
	public static void main(String[] args) {
		
		//DOMConfigurator.configureAndWatch("/config/log4j.xml");
		
		// Configurations aus der config.properties auslesen
		Config config= Config.getInstance();
		
		//Unzipper fuer den ODG-Container
		Unzip uz = new Unzip();
		
		//XML-Parser fuer die conten.xml
		OOXMLParser parser = new OOXMLParser();
		
		// CadHandler managed alle Convertierungsvorgaenge in CAD
		CadHandler cadHandler = new CadHandler();
		
		try
		{
			config.readConfigs();
		}
		catch (OO2CADException e) 
		{
			log.error("Fehler! Konfigurationsdatei nicht gefunden!");
		}

		//Setzen der benoetigten Variablen. Dies geschieht nachher alles ueber die Gui!
		config.setSourceFilePath("h:\\openoffice.odg");
		config.setDestFilePath("");//wird bisher noch statisch gesetzt
		config.setScaleInc(1);
		config.setScaleDec(1);
		config.setOffSetX(0);
		config.setOffSetY(0);
		
		log.info("OpenOffice datei eingelesen! Pfad: " + config.getSourceFilePath());
		
		// aus der *.odg-Datei die content.xml holen		
		try {
			
			//File ooXmlContent = new File(config.getProperties().getProperty("xmlFileName"));
			File ooXmlContent =	uz.extractFile(config.getSourceFilePath(), config.getProperties().getProperty("xmlFileName"));
			
			// File ooXmlContent = uz.extractFile(datei,Config.XML_FILE_NAME);
		
			// File an Parser uebergeben
			parser.parseFile(ooXmlContent);
			
			Vector<Shape> shapeList = parser.getXmlHandler().getShapeList();
			
			log.info("Es wurde(n) " + shapeList.size() + " Zeichenobjekte gefunden!");
			
			cadHandler.createCadCode(shapeList);
			
		} catch (OO2CADException e) {
			
			log.error(e.getMessage());
			log.error(e.getStackTrace());
			System.exit(0);
		}
		
		log.info("CAD-Datei wurde erfolgreich erstellt");
		System.out.println("CAD-Code wurde erfolgreich erstellt!");
		
	}
}
