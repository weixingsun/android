osmconvert --drop-author --drop-version --drop-ways --drop-relations new-zealand-latest.osm -o=nz_s.osm
osmfilter nz_s.osm -o=nz_poi.osm --drop-ways --drop-relations --ignore-dependencies --keep="amenity= and name=" --keep="historic= and name=" --keep="tourism= and name=" --keep="shop= and name=" --keep="office= and name=" --keep="leisure= and name=" --keep="craft= and name=" --keep="boundary= and name=" --keep="building= and name=" --keep="railway= and name=" --keep="admin_level= and name="
osmconvert nz_poi.osm --csv-headline -o=nz_poi.csv --all-to-nodes --csv="@lat @lon name website" --csv-separator=,

sqlite3 poi.db
.separator ','
create virtual table poi using fts3(lat DECIMAL(10,7),lng DECIMAL(10,7),pname TEXT,website varchar(80));
CREATE VIRTUAL TABLE poi2 (lat DECIMAL(10,7),lng DECIMAL(10,7),name TEXT,website varchar(80));
.import nz_poi.csv poi
select * from poi where pname like 'Peppers%' LIMIT 0,10;
select * from poi where pname match 'Peppers' LIMIT 0,10;