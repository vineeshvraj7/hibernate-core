package org.hibernate.spatial.dialect.sqlserver.convertors;

import com.vividsolutions.jts.geom.Geometry;

import org.hibernate.spatial.dialect.Translator;
import org.hibernate.spatial.jts.mgeom.MGeometryFactory;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/22/12
 */
abstract class SqlServerToGeometryTranslator<G extends Geometry> implements Translator<SqlServerGeometry, G> {

	protected final MGeometryFactory mGeometryFactory;

	SqlServerToGeometryTranslator(MGeometryFactory factory) {
		mGeometryFactory = factory;
	}

	public G translate(SqlServerGeometry value) {
		if ( value == null ) {
			return null;
		}
		if ( !accepts( value ) ) {
			throw new IllegalArgumentException(
					getClass().getSimpleName() + " can't decode object of type " + value.getClass()
							.getCanonicalName()
			);
		}
		if ( value.isEmpty() ) {
			return decodeEmpty( value );
		}
		return translatePart( value, 0 );
	}

	protected abstract G translatePart(SqlServerGeometry value, int i);

	protected abstract G decodeEmpty(SqlServerGeometry value);


	@Override
	public boolean accepts(SqlServerGeometry nativeGeom) {
		return nativeGeom.openGisType() == getOpenGisType();
	}

	boolean accepts(OpenGisType type) {
		return getOpenGisType() == type;
	}

	abstract OpenGisType getOpenGisType();

	protected MGeometryFactory getGeometryFactory() {
		return mGeometryFactory;
	}
}
