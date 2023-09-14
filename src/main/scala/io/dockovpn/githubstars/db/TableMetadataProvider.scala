package io.dockovpn.githubstars.db

import io.dockovpn.githubstars.domain.StarsCount
import io.dockovpn.metastore.provider.{AbstractTableMetadataProvider, TableMetadata}
import slick.jdbc.GetResult

import scala.reflect.ClassTag

class TableMetadataProvider extends AbstractTableMetadataProvider {
  override def getTableMetadata[V](implicit evidence$1: ClassTag[V]): TableMetadata = new TableMetadata(
    tableName = "github_stars_count",
    fieldName = "record_id",
    rconv = GetResult { r =>
      r.skip
      StarsCount(r.<<, r.<<, r.<<, r.<<)
    }
  )
}
