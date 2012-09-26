/*
 * This file is part of Hibernate Spatial, an extension to the
 *  hibernate ORM solution for spatial (geographic) data.
 *
 *  Copyright © 2007-2012 Geovise BVBA
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.hibernate.spatial.dialect.sqlserver.translators;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;

import org.hibernate.spatial.dialect.Translator;

/**
 * Serializes a JTS <code>Geometry</code> to a byte-array.
 *
 * @author Karel Maesen, Geovise BVBA.
 */
public class GeometryTranslators {

	final private static List<GeometryToSqlServerTranslator> TRANSLATORS = new ArrayList<GeometryToSqlServerTranslator>();


	static {
		//Encoders
		TRANSLATORS.add( new PointToSqlServerGeometryTranslator() );
		TRANSLATORS.add( new LineStringToSqlServerGeometryTranslator() );
		TRANSLATORS.add( new PolygonToSqlServerGeometryTranslator() );
		TRANSLATORS.add( new MultiPointToSqlServerGeometryTranslator() );
		TRANSLATORS.add( new GeometryCollectionToSqlServerTranslator<MultiLineString>( OpenGisType.MULTILINESTRING ) );
		TRANSLATORS.add( new GeometryCollectionToSqlServerTranslator<MultiPolygon>( OpenGisType.MULTIPOLYGON ) );
		TRANSLATORS.add( new GeometryCollectionToSqlServerTranslator<GeometryCollection>( OpenGisType.GEOMETRYCOLLECTION ) );

	}

	static Translator<Geometry, SqlServerGeometry> getTranslator(Geometry geom) {
		for ( GeometryToSqlServerTranslator translator : TRANSLATORS ) {
			if ( translator.accepts( geom ) ) {
				return translator;
			}
		}
		throw new IllegalArgumentException( "No Translator for type " + geom.getGeometryType() );
	}

	public static <T extends Geometry> byte[] translate(T geom) {
		Translator<Geometry, SqlServerGeometry> translator = getTranslator( geom );
		SqlServerGeometry sqlServerGeometry = translator.translate( geom );
		return SqlServerGeometry.serialize( sqlServerGeometry );
	}

}
