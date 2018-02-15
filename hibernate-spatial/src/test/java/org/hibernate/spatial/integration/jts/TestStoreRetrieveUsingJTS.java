/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.spatial.integration.jts;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import org.jboss.logging.Logger;

import org.hibernate.dialect.Dialect;
import org.hibernate.spatial.HSMessageLogger;
import org.hibernate.spatial.integration.AbstractTestStoreRetrieve;
import org.hibernate.spatial.testing.GeometryEquality;
import org.hibernate.spatial.testing.JTSGeometryEquality;
import org.hibernate.spatial.testing.SpatialDialectMatcher;
import org.hibernate.spatial.testing.TestDataElement;

import org.hibernate.testing.Skip;

/**
 * This testsuite-suite class verifies whether the <code>Geometry</code>s retrieved
 * are equal to the <code>Geometry</code>s stored.
 */
@Skip(condition = SpatialDialectMatcher.class, message = "No Spatial Dialect")
public class TestStoreRetrieveUsingJTS extends AbstractTestStoreRetrieve<Geometry, GeomEntity> {

	private static final HSMessageLogger LOG = Logger.getMessageLogger(
			HSMessageLogger.class,
			TestStoreRetrieveUsingJTS.class.getName()
	);


	protected HSMessageLogger getLogger() {
		return LOG;
	}


	@Override
	protected GeometryEquality<Geometry> getGeometryEquality() {
		return new JTSGeometryEquality();
	}

	@Override
	protected Class<GeomEntity> getGeomEntityClass() {
		return GeomEntity.class;
	}

	@Override
	protected GeomEntity createFrom(
			TestDataElement element, Dialect dialect) {
		try {
			return GeomEntity.createFrom( element, dialect );
		}
		catch (ParseException e) {
			throw new RuntimeException( e );
		}

	}
}
