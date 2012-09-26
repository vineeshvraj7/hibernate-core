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

import org.hibernate.spatial.dialect.Translator;
import org.hibernate.spatial.jts.JTS;
import org.hibernate.spatial.jts.mgeom.MGeometryFactory;

/**
 * Decodes SQL Server Geometry objects to JTS <code>Geometry</code>s.
 *
 * @author Karel Maesen, Geovise BVBA.
 */
public class SqlServerTranslators {

	final private static List<SqlServerToGeometryTranslator<? extends Geometry>> TRANSLATORS = new ArrayList<SqlServerToGeometryTranslator<? extends Geometry>>();

	static {
		MGeometryFactory factory = JTS.getDefaultGeometryFactory();

		//Decoders
		TRANSLATORS.add( new SqlServerToPointTranslator( factory ) );
		TRANSLATORS.add( new SqlServerToLineStringTranslator( factory ) );
		TRANSLATORS.add( new SqlServerToPolygonTranslator( factory ) );
		TRANSLATORS.add( new SqlServerToMultiLineStringTranslator( factory ) );
		TRANSLATORS.add( new SqlServerToMultiPolygonTranslator( factory ) );
		TRANSLATORS.add( new SqlServerToMultiPointTranslator( factory ) );
		TRANSLATORS.add( new SqlServerToGeometryCollectionTranslator<GeometryCollection>( factory ) {
			@Override
			public Class<GeometryCollection> getTranslatedType() {
				return GeometryCollection.class;
			}
		});

	}


	private static SqlServerToGeometryTranslator<? extends Geometry> getTranslator(SqlServerGeometry object) {
		for ( SqlServerToGeometryTranslator<? extends Geometry> translator : TRANSLATORS ) {
			if ( translator.accepts( object ) ) {
				return translator;
			}
		}
		throw new IllegalArgumentException( "No translator to JTS for type " + object.openGisType() );
	}

	/**
	 * Decodes the SQL Server Geometry object to its JTS Geometry instance
	 *
	 * @param raw
	 *
	 * @return
	 */
	public static Geometry translate(byte[] raw) {
		SqlServerGeometry sqlServerGeom = SqlServerGeometry.deserialize( raw );
		SqlServerToGeometryTranslator<? extends Geometry> translator = getTranslator( sqlServerGeom );
		return translator.translate( sqlServerGeom );
	}

	/**
	 * Returns the <code>Translator</code> capable of translating an object of the specified OpenGisType
	 * to JTS <code>Geometry</code>.
	 *
	 * @param type OpenGisType for which a decoder is returned
	 *
	 * @return
	 */
	public static Translator<SqlServerGeometry, ? extends Geometry> getTranslator(OpenGisType type) {
		for ( SqlServerToGeometryTranslator<? extends Geometry> translator : TRANSLATORS ) {
			if ( translator.accepts( type ) ) {
				return translator;
			}
		}
		throw new IllegalArgumentException( "No translator to JTS for type " + type );
	}

}
