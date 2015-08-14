/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.odps;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aliyun.odps.commons.transport.OdpsTestUtils;

public class TablesTest extends TestBase {

  private String tableName = this.getClass().getSimpleName() + "_table_name_for_testTables";

  @Before
  public void setUp() throws Exception {
    odps = OdpsTestUtils.newDefaultOdps();

    TableSchema schema = new TableSchema();
    schema.addColumn(new Column("c1", OdpsType.BIGINT));
    schema.addColumn(new Column("c2", OdpsType.BOOLEAN));
    schema.addColumn(new Column("c3", OdpsType.DATETIME));
    schema.addColumn(new Column("c4", OdpsType.STRING));

    schema.addPartitionColumn(new Column("p1", OdpsType.BIGINT));
    schema.addPartitionColumn(new Column("p2", OdpsType.STRING));

    odps.tables().create(odps.getDefaultProject(), tableName, schema);
  }


  @After
  public void tearDown() throws Exception {
    odps.tables().delete(tableName);
  }

  @Test
  public void testCreateTable() throws OdpsException {
    UUID id = UUID.randomUUID();
    String tableName = id.toString().replace("-" , "");
    TableSchema schema = new TableSchema();
    schema.addColumn(new Column("c1", OdpsType.BIGINT));
    schema.addColumn(new Column("_c2", OdpsType.STRING, "_comment here"));
    schema.addPartitionColumn(new Column("p1", OdpsType.STRING));
    schema.addPartitionColumn(new Column("_p2", OdpsType.STRING, "_comment here"));
    odps.tables().create(odps.getDefaultProject(), "testCreateTable" + tableName, schema, "_table comment", false);
    Table table = odps.tables().get("testCreateTable" + tableName);
    assertEquals(table.getComment(), "_table comment");
    TableSchema returnSchema = table.getSchema();
    assertEquals(returnSchema.getColumns().size(), 2L);
    assertEquals(returnSchema.getColumn("c1").getName(), "c1");
    assertEquals(returnSchema.getColumn("c1").getType(), OdpsType.BIGINT);
    assertEquals(returnSchema.getColumn("c1").getComment(), "");
    assertEquals(returnSchema.getColumn("_c2").getName(), "_c2");
    assertEquals(returnSchema.getColumn("_c2").getType(), OdpsType.STRING);
    assertEquals(returnSchema.getColumn("_c2").getComment(), "_comment here");

    assertEquals(returnSchema.getPartitionColumn("p1").getName(), "p1");
    assertEquals(returnSchema.getPartitionColumn("p1").getType(), OdpsType.STRING);
    assertEquals(returnSchema.getPartitionColumn("p1").getComment(), "");
    assertEquals(returnSchema.getPartitionColumn("_p2").getName(), "_p2");
    assertEquals(returnSchema.getPartitionColumn("_p2").getType(), OdpsType.STRING);
    assertEquals(returnSchema.getPartitionColumn("_p2").getComment(), "_comment here");


  }

  @Test(expected = OdpsException.class)
  public void testCreateTableCrossProject() throws OdpsException {
    TableSchema schema = new TableSchema();
    schema.addColumn(new Column("c1", OdpsType.BIGINT));
    schema.addColumn(new Column("c2", OdpsType.BOOLEAN));
    schema.addColumn(new Column("c3", OdpsType.DATETIME));
    schema.addColumn(new Column("c4", OdpsType.STRING));

    schema.addPartitionColumn(new Column("p1", OdpsType.BIGINT));
    schema.addPartitionColumn(new Column("p2", OdpsType.STRING));

    odps.tables().create("NOT_EXIST_PROJECT", tableName, schema);
  }


  @Test
  public void testGetString() {
    Table table = odps.tables().get(tableName);
    Assert.assertNotNull(table);
  }

  @Test
  public void testExistsString() throws OdpsException {
    boolean flag = odps.tables().exists(tableName);
    assertEquals(flag, true);

    flag = odps.tables().exists("table_name_for_test_not_exists");
    assertEquals(flag, false);
  }

  @Test
  public void testIteratorTableFilter() {
    TableFilter tableFilter = new TableFilter();
    tableFilter.setName("user");
    Iterator<Table> iterator = odps.tables().iterator(tableFilter);
    while (iterator.hasNext()) {
      Table table = iterator.next();
      Assert.assertNotNull(table.getName());
    }
  }

  @Test
  public void testIteratorString() {
    Iterator<Table> iterator = odps.tables().iterator();
    if (iterator.hasNext()) {
      Assert.assertNotNull(iterator.next().getName());
    }
  }

  @Test
  public void testDeleteString() throws OdpsException {
    //Already Test in method tearDown() ,please don't delete again
  }

}
