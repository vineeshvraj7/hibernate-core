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
import com.vividsolutions.jts.geom.Point;

import org.hibernate.spatial.jts.mgeom.MGeometryFactory;

/**
 * @author Karel Maesen, Geovise BVBA.
 *         Date: Nov 2, 2009
 */
class SqlServerToPointTranslator extends SqlServerToGeometryTranslator<Point> {

	public SqlServerToPointTranslator(MGeometryFactory factory) {
		super( factory );
	}

	@Override
	public Class<Point> getOutputType() {
		return Point.class;
	}

	@Override
	protected Point decodeEmpty(SqlServerGeometry encoded) {
		Point pnt = getGeometryFactory().createPoint( (Coordinate) null );
		pnt.setSRID( encoded.getSrid() );
		return pnt;
	}

	@Override
	public Point translatePart(SqlServerGeometry nativeGeom, int shapeIndex) {
		if ( nativeGeom.isEmptyShape( shapeIndex ) ) {
			return getGeometryFactory().createPoint( (Coordinate) null );
		}
		int figureOffset = nativeGeom.getFiguresForShape( shapeIndex ).start;
		int pntOffset = nativeGeom.getPointsForFigure( figureOffset ).start;
		Point result = createPoint( nativeGeom, pntOffset );
		if ( shapeIndex == 0 ) {
			result.setSRID( nativeGeom.getSrid() );
		}
		return result;
	}

	@Override
	OpenGisType getOpenGisType() {
		return OpenGisType.POINT;
	}


	private Point createPoint(SqlServerGeometry nativeGeom, int pntOffset) {
		return getGeometryFactory().createPoint( nativeGeom.getCoordinate( pntOffset ) );
	}


}
