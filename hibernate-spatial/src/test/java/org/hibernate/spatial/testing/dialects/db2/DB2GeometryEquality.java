
package org.hibernate.spatial.testing.dialects.db2;

import com.vividsolutions.jts.geom.Geometry;

import org.hibernate.spatial.testing.GeometryEquality;

public class DB2GeometryEquality extends GeometryEquality {


	@Override
	public boolean test(Geometry geom1, Geometry geom2) {
		return this.test( geom1, geom2, true );
	}


}
