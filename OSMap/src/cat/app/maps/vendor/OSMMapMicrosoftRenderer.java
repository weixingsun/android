package cat.app.maps.vendor;
import org.osmdroid.ResourceProxy;
import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

public class OSMMapMicrosoftRenderer extends OnlineTileSourceBase {

	protected static final char[] NUM_CHAR = { '0', '1', '2', '3' };
	private final int mOrdinal;

	public OSMMapMicrosoftRenderer(String aName, final string aResourceId, int aZoomMinLevel,
                      int aZoomMaxLevel, int aMaptileZoom, String aImageFilenameEnding, int ordinal,
			String ...aBaseUrl) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aMaptileZoom, aImageFilenameEnding, aBaseUrl);
        mOrdinal = ordinal;
	}

	@Override
	public String localizedName(ResourceProxy proxy) {
		return name();
	}

	@Override
	public int ordinal() {
		return mOrdinal;
	}

	@Override
	public String getTileURLString(MapTile aTile) {
        int zoom = aTile.getZoomLevel();
        int x = aTile.getX();
        int y = aTile.getY();
        String tileNum = encodeQuadTree(zoom, x, y);
		return new StringBuilder().append(getBaseUrl()).append(tileNum)
            .append(imageFilenameEnding()).append("?g=45")
            .toString();
	}

	/**
	 * See: http://msdn.microsoft.com/en-us/library/bb259689.aspx
	 * @param zoom
	 * @param tilex
	 * @param tiley
	 * @return quadtree encoded tile number
	 * 
	 */
	public static String encodeQuadTree(int zoom, int tilex, int tiley) {
		char[] tileNum = new char[zoom];
		for (int i = zoom - 1; i >= 0; i--) {
			// Binary encoding using ones for tilex and twos for tiley. if a bit
			// is set in tilex and tiley we get a three.
			int num = (tilex % 2) | ((tiley % 2) << 1);
			tileNum[i] = NUM_CHAR[num];
			tilex >>= 1;
			tiley >>= 1;
		}
		return new String(tileNum);
	}
}