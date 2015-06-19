osmconvert --drop-author --drop-version --all-to-nodes --drop-relations new-zealand-latest.osm -o=nz_s_way.osm
osmfilter nz_s_way.osm -o=nz_poi_way.osm --drop-relations --ignore-dependencies --keep="amenity= and name=" --keep="historic= and name=" --keep="tourism= and name=" --keep="shop= and name=" --keep="office= and name=" --keep="leisure= and name=" --keep="craft= and name=" --keep="boundary= and name=" --keep="building= and name=" --keep="railway= and name=" --keep="admin_level= and name=" --keep="man_made=surveillance" 
osmconvert nz_poi_way.osm --csv-headline -o=nz_poi_type_all.csv --all-to-nodes --csv="@lat @lon amenity historic tourism shop office leisure craft boundary building railway admin_level man_made name" --csv-separator=,
python csv_parser.py	#loopInCsvMergeType()	#nz_poi_type_all_utf8_merge.csv
#add few CCTV names
#remove useless columns
#add admin column ahead of name
python csv_parser.py	#loopInCsvGeoNames()

cat nz_write_geonames1.csv nz_write_geonames2.csv nz_write_geonames3.csv > nz_write_geonames_123.csv
sed '/^$/d' nz_write_geonames_123.csv > nz_poi_123.csv

sqlite3 poi.db
.separator ','
create virtual table poi using fts3(lat DECIMAL(10,7),lng DECIMAL(10,7),type varchar(50),country_code char(3),admin varchar(50),pname TEXT );
#CREATE VIRTUAL TABLE poi2 (lat DECIMAL(10,7),lng DECIMAL(10,7),name TEXT,website varchar(80));
.import nz_poi_123.csv poi
select * from poi where pname like 'Peppers%' LIMIT 0,10;
select * from poi where pname match 'Peppers' LIMIT 0,10;

.separator ','
.output yes.csv
select * from poi_yes;
.output stdout

grep -a -C 5 pier new-zealand-latest.osm > pier.grep
--keep="landuse="