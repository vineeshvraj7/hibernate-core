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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import org.hibernate.spatial.jts.mgeom.MGeometryFactory;

/**
 * @author Karel Maesen, Geovise BVBA
 */
class SqlServerToPolygonTranslator extends SqlServerToGeometryTranslator<Polygon> {

	SqlServerToPolygonTranslator(MGeometryFactory factory) {
		super( factory );
	}

	@Override
	OpenGisType getOpenGisType() {
		return OpenGisType.POLYGON;
	}

	private LinearRing toLinearRing(SqlServerGeometry nativeGeom, IndexRange range) {
		Coordinate[] coordinates = nativeGeom.coordinateRange( range );
		return getGeometryFactory().createLinearRing( coordinates );
	}

	@Override
	protected Polygon decodeEmpty(SqlServerGeometry encoded) {
		Polygon pg = getGeometryFactory().createPolygon( null, null );
		pg.setSRID( encoded.getSrid() );
		return pg;
	}

	@Override
	public Polygon translatePart(SqlServerGeometry nativeGeom, int shapeIndex) {
		if ( nativeGeom.isEmptyShape( shapeIndex ) ) {
			return getGeometryFactory().createPolygon( null, null );
		}
		IndexRange figureRange = nativeGeom.getFiguresForShape( shapeIndex );
		LinearRing[] holes = new LinearRing[figureRange.length() - 1];
		LinearRing shell = null;
		for ( int figureIdx = figureRange.start, i = 0; figureIdx < figureRange.end; figureIdx++ ) {
			IndexRange pntIndexRange = nativeGeom.getPointsForFigure( figureIdx );
			if ( nativeGeom.isFigureInteriorRing( figureIdx ) ) {
				holes[i++] = toLinearRing( nativeGeom, pntIndexRange );
			}
			else {
				shell = toLinearRing( nativeGeom, pntIndexRange );
			}
		}
		Polygon pg = getGeometryFactory().createPolygon( shell, holes );
		if ( shapeIndex == 0 ) {
			pg.setSRID( nativeGeom.getSrid() );
		}
		return pg;
	}

	@Override
	public Class<Polygon> getOutputType() {
		return Polygon.class;
	}
}
