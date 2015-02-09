package cat.app.maps.vendor;

import java.io.File;
import java.util.Collections;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;

import cat.app.maps.OSM;

import android.app.Activity;

/**
 * http://www.salidasoftware.com/how-to-render-mapsforge-tiles-in-osmdroid/
 * @author Salida Software
 * Adapted from code found here : http://www.sieswerda.net/2012/08/15/upping-the-developer-friendliness/
 */
public class MapsForgeTileProvider extends MapTileProviderArray {

    public MapsForgeTileProvider(OSM osm, IRegisterReceiver receiverRegistrar, File file) {

        super(MapsForgeTileSourceOld.createFromFile(osm,file), receiverRegistrar);

        // Create the module provider; this class provides a TileLoader that
        // actually loads the tile from the map file.
        MapsForgeTileModuleProvider moduleProvider;
        moduleProvider = new MapsForgeTileModuleProvider(receiverRegistrar, file, (MapsForgeTileSourceOld) getTileSource());

        MapTileModuleProviderBase[] pTileProviderArray;
        pTileProviderArray = new MapTileModuleProviderBase[] { moduleProvider };

        // Add the module provider to the array of providers; mTileProviderList
        // is defined by the superclass.
        Collections.addAll(mTileProviderList, pTileProviderArray);
    }
    
}
