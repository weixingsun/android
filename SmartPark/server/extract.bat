osmconvert --drop-author --drop-version --drop-ways --drop-relations new-zealand-latest.osm -o=nz_s.osm
#osmconvert nz_s.osm --all-to-nodes --csv="@id @lon @lat amenity shop name" --csv-headline -o=nz.csv
#osmconvert man_made.osm --csv-headline -o=man_made.csv --all-to-nodes --csv="@id @lon @lat man_made name url" --csv-separator=,
#osmfilter nz_s.osm --keep="man_made= and name=" -o=man_made.osm
osmfilter nz_s.osm --keep="amenity= and name=" -o=amenity.osm
osmfilter nz_s.osm --keep="historic= and name=" -o=historic.osm
osmfilter nz_s.osm --keep="tourism= and name=" -o=tourism.osm
osmfilter nz_s.osm --keep="shop= and name=" -o=shop.osm
osmfilter nz_s.osm --keep="leisure= and name=" -o=leisure.osm
osmfilter nz_s.osm --keep="office= and name=" -o=office.osm
osmfilter nz_s.osm --keep="building= and name=" -o=building.osm
osmfilter nz_s.osm --keep="railway= and name=" -o=railway.osm
osmfilter nz_s.osm --keep="craft= and name=" -o=craft.osm
osmfilter nz_s.osm --keep="admin_level= and name=" -o=admin_level.osm
osmfilter nz_s.osm --keep="boundary= and name=" -o=boundary.osm

osmconvert amenity.osm --csv-headline -o=amenity.csv --all-to-nodes --csv="@id @lon @lat amenity name url" --csv-separator=,
osmconvert historic.osm --csv-headline -o=historic.csv --all-to-nodes --csv="@id @lon @lat historic name url" --csv-separator=,
osmconvert tourism.osm --csv-headline -o=tourism.csv --all-to-nodes --csv="@id @lon @lat tourism name url" --csv-separator=,
osmconvert shop.osm --csv-headline -o=shop.csv --all-to-nodes --csv="@id @lon @lat shop name url" --csv-separator=,
osmconvert leisure.osm --csv-headline -o=leisure.csv --all-to-nodes --csv="@id @lon @lat leisure name url" --csv-separator=,
osmconvert office.osm --csv-headline -o=office.csv --all-to-nodes --csv="@id @lon @lat office name url" --csv-separator=,
osmconvert building.osm --csv-headline -o=building.csv --all-to-nodes --csv="@id @lon @lat building name url" --csv-separator=,
osmconvert railway.osm --csv-headline -o=railway.csv --all-to-nodes --csv="@id @lon @lat railway name url" --csv-separator=,
osmconvert craft.osm --csv-headline -o=craft.csv --all-to-nodes --csv="@id @lon @lat craft name url" --csv-separator=,
osmconvert admin_level.osm --csv-headline -o=admin_level.csv --all-to-nodes --csv="@id @lon @lat admin_level name url" --csv-separator=,
osmconvert boundary.osm --csv-headline -o=boundary.csv --all-to-nodes --csv="@id @lon @lat boundary name url" --csv-separator=,
