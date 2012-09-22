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

package org.hibernate.spatial.dialect.sqlserver.convertors;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

import org.hibernate.spatial.jts.mgeom.MGeometryFactory;

abstract class SqlServerToGeometryCollectionTranslator<T extends GeometryCollection>
		extends SqlServerToGeometryTranslator<T> {

	SqlServerToGeometryCollectionTranslator(MGeometryFactory factory) {
		super( factory );
	}

	@Override
	OpenGisType getOpenGisType() {
		return OpenGisType.GEOMETRYCOLLECTION;
	}

	@Override
	protected T decodeEmpty(SqlServerGeometry encoded) {
		T geometry = createGeometry( (List<Geometry>) null, false );
		geometry.setSRID( encoded.getSrid() );
		return geometry;
	}


	@Override
	public T translatePart(SqlServerGeometry nativeGeom, int shapeIndex) {
		if ( nativeGeom.isEmptyShape( shapeIndex ) ) {
			return createGeometry( (List<Geometry>) null, false );
		}
		int startChildIdx = shapeIndex + 1;
		List<Geometry> geometries = new ArrayList<Geometry>( nativeGeom.getNumShapes() );
		for ( int childIdx = startChildIdx; childIdx < nativeGeom.getNumShapes(); childIdx++ ) {
			if ( !nativeGeom.isParentShapeOf( shapeIndex, childIdx ) ) {
				continue;
			}
			SqlServerToGeometryTranslator<?> decoder = SqlServerTranslators.decoderFor(
					nativeGeom.getOpenGisTypeOfShape(
							childIdx
					)
			);
			Geometry geometry = decoder.translatePart( nativeGeom, childIdx );
			geometries.add( geometry );
		}
		GeometryCollection geom = createGeometry( geometries, nativeGeom.hasMValues() );
		if ( shapeIndex == 0 ) {
			geom.setSRID( nativeGeom.getSrid() );
		}
		return getOutputType().cast( geom );
	}


	abstract protected T createGeometry(List<Geometry> geometries, boolean hasM);


}
