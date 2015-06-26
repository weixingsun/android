#!/usr/bin/python

#python setup.py install	#BeautifulSoup
import os
import re
import csv
import sys
import json
import string
import urllib.request
import xml.etree.ElementTree as xml
from bs4 import BeautifulSoup
from pprint import pprint
from collections import defaultdict


def getAdminGoogle(txt):
	data = json.loads(str(txt))
	try:
		for i in data.get('results')[0].get('address_components'):
			#types=i.get('types')
			#print("types: ", types)
			if(len(i.get('types'))>0 and i.get('types')[0] == 'administrative_area_level_1'):	#administrative_area_level_1,locality
				return i.get('short_name')
	except (RuntimeError,IndexError):
		pass
	return 'admin'
#<geonames>
#	<countrySubdivision>
#		<countryCode>NZ</countryCode>
#		<countryName>New Zealand</countryName>
#		<adminCode1>F7</adminCode1>
#		<adminName1>Otago</adminName1>
#		<code type="FIPS10-4">F7</code>
#		<code type="ISO3166-2">OTA</code>
#		<distance>0.0</distance>
#	</countrySubdivision>
#</geonames>
def getAdminGeoNames(txt):
	try:
		tree = xml.fromstring(txt)
		#doc = tree.getroot()
		#thingy = tree.find('adminName1')
		#root[0][1].text
		for admin in tree.iter('adminName1'):
			if(len(admin.text)>0):
				return admin.text
		#for child in tree[0]:
		#	print(child.tag, child.attrib, child.text)
		return ''
	except (RuntimeError,IndexError):
		pass
	return 'admin'

def getHtml3(url):
	with urllib.request.urlopen(url) as url:
		s = url.read()
	return s
	
def loopInCsvGeoNames(src,dst):
	with open(dst, 'w') as f: # output csv file: wb means use binary mode
		writer = csv.writer(f)
		with open(src,'r') as csvfile: # input csv file
			reader = csv.reader(csvfile)
			for row in reader:  
				lat,lng,name,country,admin,website = getValue(row)
				url=geo_url+'lat='+lat+'&lng='+lng
				json_text=getHtml3(url).decode("utf-8")
				#print(url)
				admin=getAdminGeoNames(json_text)
				#print(row[0]+","+row[1]+","+row[2]+","+row[3]+","+admin)
				row[4] = admin # edit the 5th column 
				writer.writerow(row)
				
def loopInCSVGoogle(src,dst):
	with open(dst, 'w') as f: # output csv file: wb means use binary mode
		writer = csv.writer(f)
		with open(src,'r') as csvfile: # input csv file
			reader = csv.reader(csvfile)
			for row in reader:  
				lat,lng,name,country,admin,website = getValue(row)
				if(row[4] != 'admin'):
					continue
				url=google_url+lat+','+lng
				json_text=getHtml3(url).decode("utf-8")
				#print(url)
				admin=getAdmin(json_text)
				#print(row[0]+","+row[1]+","+row[2]+","+row[3]+","+admin)
				row[4] = admin # edit the 5th column 
				writer.writerow(row)

def getValue(row):
	lat=row[0]
	lng=row[1]
	name=row[2]
	country=row[3]
	admin=row[4]
	website=row[5]
	return (lat,lng,name,country,admin,website)
def getKeyValue(row):
	for (k,v) in row.items():
		if(k=='lat'):
			lat=v
		if(k=='lng'):
			lng=v
	return (lat,lng)
def printCatLinks(url,dist_file):
	html_page = getHtml3(url)
	soup = BeautifulSoup(html_page)
	for link in soup.findAll('a', href=re.compile('^http://www.ebay.com/sch/Watches')):
		appendTxt(dist_file,link.get('href'))

#def printGoodLinks(url,dist_file):
#	html_page = getHtml3(url)
#	soup = BeautifulSoup(html_page)
#	for link in soup.findAll('a', href=re.compile('^http://www.ebay.com/itm')):
#		appendTxt(dist_file,link.get('href'))
def findGood(cat_url,good_file):
	str_html=getHtml3(cat_url)
	soup = BeautifulSoup(str_html)
	for link in soup.findAll('a', href=re.compile('^http://www.ebay.com/itm')):
		str_href=link.get('href')
		appendTxt(good_file,str_href)
def printGoodLinks(cat_file,good_file):
	loopInFile(findGood,cat_file,good_file)

def appendTxt(file,text):
	with open(file, "a") as out_file:
		out_file.write(str(text)+"\n")

def loopInFile(func,src_file,dist_file):
	with open(src_file) as f:
		for line in f:
			func(line,dist_file)
def findSeller(good_url,dist_file):
	str_html=getHtml3(good_url)
	soup = BeautifulSoup(str_html)
	for link in soup.findAll('a', href=re.compile('^http://www.ebay.com/usr')):
		str_href=link.get('href')
		userid=getUserByUrl(str_href)
		appendTxt(dist_file,userid)
	
def getUserByUrl(url):
	forehead=url.split('?')[0]
	userid=forehead.split('/')[-1]
	return userid
	
def deleteFile(file_path):
	try:
		os.remove(file_path)
	except IOError as e:
		print("OS error: {0}".format(e))
	except ValueError:
		print("Could not convert data to an integer.")
	except:
		print("Unexpected error:", sys.exc_info()[0])
		raise
#def findGoodsLinks(html):
	#main entry: 
	#category entry
	#<a class="ranc" href="http://www.ebay.com/itm/Rolex-Stainless-Steel-Explorer-II-Date-Watch-SEL-Oyster-Band-Black-Dial-16570-/201361523890?_trkparms=%26rpp_cid%3D532356f389917e7e85ce6ab6%26rpp_icid%3D532352c132ed799f270bd572">
	#<a href="http://www.ebay.com/usr/swisswatchexpo?_trksid=p2047675.l2559" aria-label="Member ID:&nbsp;swisswatchexpo"> <span class="mbg-nw">swisswatchexpo</span></a>

#############################################################################
#num=sys.argv[len(sys.argv)-1]
#cat_file='C:/Users/SUN/Desktop/www/ebay_watch/temp/cat'+num+'.txt'
#good_file='C:/Users/SUN/Desktop/www/ebay_watch/temp/links'+num+'.txt'
test_file='C:/Users/SUN/Desktop/www/google_poi_admin/dst/test.xml'
test_admin_file='C:/Users/SUN/Desktop/www/google_poi_admin/dst/test_admin.txt'
google_url='http://maps.googleapis.com/maps/api/geocode/json?latlng='  #39.761442,-104.801158	#-43.525827,172.584113	#-46.3865651,169.7818566
mapquest_url='http://www.mapquestapi.com/geocoding/v1/reverse?key=YOUR_KEY_HERE&callback=renderReverse&location='	#39.761442,-104.801158
test_url='http://maps.googleapis.com/maps/api/geocode/json?latlng=-46.3865651,169.7818566'
test_url2='http://api.geonames.org/countrySubdivision?username=weixingsun&lat=-46.3865651&lng=169.7818566'
geo_url='http://api.geonames.org/countrySubdivision?username=weixingsun&'	#lat=-46.3865651&lng=169.7818566
#osm_url='http://nominatim.openstreetmap.org/search?q=45.8364043,24.8345179&format=xml&addressdetails=1'

#deleteFile(test_admin_file)
#json_text = getHtml3(test_url2).decode("utf-8")
#appendTxt(test_file,json_text)
#admin = getAdminGeoNames(json_text)
#columns = defaultdict(list)
loopInCsvGeoNames('nz.csv','nz_write_geonames.csv')


#print(columns['name'])
#print(columns['lat'])
#print(columns['lng'])
#appendTxt(test_admin_file,admin)
#printCatLinks(cat_url,cat_file)
#printGoodLinks(cat_file,good_file)
#loopInFile(findSeller,good_file,user_file)