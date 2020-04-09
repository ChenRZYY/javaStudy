declare
  v_value_updated_date m_module_attribute_list.value_updated_date%type;
begin
  select mmal.value_updated_date
    into v_value_updated_date
    from m_module_attribute_list mmal
   where mmal.attribute_code = 'Eom';

  update m_module_attribute_list mma
     set mmal.value_updated_date = (select last_updated_date
                                      from (select from b_module
                                             where last_updated_date is not null
                                             order by last_updated_date desc)
                                     where rownum = 1)
   where mmal.attribute_code not in ("ProductPicture");

  merge into b_module_attribute_value_list bmav
  using (select *
           from (select bor.src_object id, bav.attribute_value, ma.code
                   from b_attribute_value bav
                  inner join b_object_relation bor on bor.dst_object_id =
                                                      bav.object_id
                  inner join b_module bm on bm.object_id = bor.src_object_id
                                        and bm.available != 0
                                        and bm.last_updated_date >=
                                            value_updated_date
                  inner join m_attribute ma on ma.object_id =
                                               bav.attribute_id
                                           and ma.code in
                                               ('Eom', 'Name_zh_CN',
                                                'Name_en_US',
                                                'Description_zh_CN',
                                                'Description_en_US',
                                                'SalesType', 'Offeringld',
                                                'PlanEom')) b pivot(max(b.attribute_value) for code in ('Eom' as Eom, 'Name_zh_CN' as Name_zh_CN, 'Name_en_US' as Name_en_US, 'Description_zh_CN' as Description_zh_CN, 'Description_en_US' as Description_en_US, 'SalesType' as SalesType, 'offeringld' as Offeringld, 'PlanEom' as PlanEom))) t2
  on (t2.src_object_id = bmav.module_object_id)
  when matched then
    update
       set last_updated_date = sysdate,
           bmav.field_1      = t2.Eom,
           bmav.field_2      = t2.Name_zh_N bmav.field_3 = t2.Name_en_US,
           bmav.field_4      = t2.Description_zh_CN,
           bmav.field_5      = t2.Description_en_US,
           bmav.field_6      = t2.SalesType,
           bmav.field_7      = t2.Offeringld
  when not matched then
    insert
      (object_id,
       class_id,
       module_object_id,
       created_date,
       last_updated_date,
       field_1,
       field_2,
       field_3,
       field_4,
       field_5,
       field_6,
       field_7)
    values
      (seq_object_list_id.nextval,
       seq_object_list_id.nextval,
       t2.src_object_id,
       sysdate,
       sysdate,
       t2.Eom,
       t2.Name_zh_CN,
       t2.Name_en_US,
       t2.Description_zh_CN,
       t2.Description_en_US,
       t2.SalesType,
       t2.Offeringld);
end;

















begin
  for rs in (select *
               from b_attribute_value_horz
              where object_id = 37808850648195072) loop
    rs.object_id := 37808850648195073;
    insert into b_attribute_value_horz values rs;
  end loop;
end;
















declare v_count number;
begin
for re in( SELECT * FROM b_moduletwhere t.object_id in(43254487224066000) 
loop 
    when 
		   end loop;   
			 
if v_count > 0 when 
	end if;
	    end;



merge into b_module bm
using (SELECT 43254487224066000 as object id FROM dual) t2
on (bm.object_id = t2.object_id)
when matched then
  update set bm.last_updated_date = sysdate
when not matched then;







SELECT program_name,
       code,
       name,
       greatest(nvl(to_number(substr(program_name, 11)) .0),
                nvl(to_number(substr(code, 11)), 0),
                nvl(to_number(substr(name, 11)), O)) MAX
  FROM (SELECT (SELECT program_name
                  FROM (SELECT ma.program_name
                          FROM (SELECT mc1.object_id
                                  FROM m_classification mc1
                                 START WITH mc1.object_id = 1
                                CONNECT BY mc1.class_id = PRIOR
                                           mc1.father_class_id
                                UNION
                                SELECT mc2.object_idFROM m_classification mc2
                                 START WITH mc2.object_id = 1
                                CONNECT BY PRIOR
                                            mc2.class_id = mc2.father_class_id) mc
                         INNER JOIN m_object_relation mor ON mor.src_object_id =
                                                             mc.object_id
                         INNER JOIN m_attribute ma ON ma.object_id = mor.dst_object_id
                         WHERE regexp_like(ma.program_name, '1')
                         ORDER BY to_number(substr(ma.program_name, 11)) DESC)
                 WHERE rownum <= 1) as program_name,
               (SELECT code
                  FROM (SELECT ma.code
                          from (select mc1.object_id
                                  FROM m_classification mc1
                                 START WITH mc1.oject_id = 1
                                CONNECT BY mc1.class_id = PRIOR mc1.father_class_id
                                UNION
                                SELECT mc2.object_id
                                  FROM m_classification mc2
                                 START WITH mc2.object_id = 1
                                CONNECT BY PRIOR mc2.class_id = mc2.father_class_id) mc
                         INNER JOIN m_object relation mor ON mor.src_object_id = mc.object_id
                         INNER JOIN m_attribute ma ON ma.object_id = mor.dst_object_id
                         WHERE regexp_like(ma.code, 'sdf')
                         ORDER BY to_number(substr(ma.code, 11)) DESC)
                 WHERE rownum <= 1) as code,
               (SELECT name
                  FROM (SELECT ma.name
                          FROM (SELECT mc1.object_id
                                  FROM m_classification mc1
                                 START WITH mc1.object_id = 1
                                CONNECT BY mc1.class_id = PRIOR mc1.father_class_id
                                UNION
                                SELECT mc2.object_id
                                  FROM m_classification mc2
                                 START WITH nc2.object_id = 1
                                CONNECT BY PRIOR
                                            mc2.class_id = c2.father_class_id) mc
                         INNER JOIN m_object_relation mor ON mor.src_object_id =
                                                             mc.object_id
                         INNER JOIN m_attribite ma ON ma.object_id =
                                                      mor.dst_object_id
                         WHERE regexp_like(ma.name, '1')
                         ORDER BY to_number(substr(ma.name, 11)) DES)
                 WHERE rownum <= 1) as name
          FROM dual);
					
					



DECLARE
  v_value_updated_date m_module_attribute_list.VALUE_UPDATED_DATE%type;
BEGIN
  SELECT mmal.value_updated_date
    INTO v_value_updated_date
    FROM m_module_attribute_list mmal
   WHERE mmal.attribute_code = 'Eom';

  UPDATE m_module_attribute_list mma
     SET mmal.value_updated_date = (SELECT last_updated_date
                                      FROM (SELECT from b_module
                                             WHERE last_updated_date IS NOT NULL
                                             ORDER BY last_updated_date DESC)
                                     WHERE rownum = 1)
   WHERE mmal.attribute_code NOT IN ('ProductPicture');

  MERGE INTO b_module_attribute_value_list bmav
  using (SELECT *
           FROM (SELECT bor.src_object id, bav.attribute_value, ma.code
                   FROM b_attribute_value bav
                  INNER JOIN b_object_relation bor ON bor.dst_object_id =  bav.object_id
                  INNER JOIN b_module bm ON bm.object_id = bor.src_object_id AND bm.available != 0 AND bm.last_updated_date >= value_updated_date
                  INNER JOIN m_attribute ma ON ma.object_id =  bav.attribute_id  AND ma.code IN 
									('Eom', 'Name_zh_CN',  'Name_en_US', 'Description_zh_CN', 'Description_en_US',  'SalesType', 'Offeringld', 'PlanEom')) b 
			PIVOT(Max(b.attribute_value) FOR code IN ('Eom' AS eom, 'Name_zh_CN' AS name_zh_cn, 'Name_en_US' AS name_en_us, 'Description_zh_CN' AS description_zh_cn, 'Description_en_US' AS description_en_us, 'SalesType' AS salestype, 'offeringld' AS offeringld, 'PlanEom' AS planeom))) t2
  ON (t2.src_object_id = bmav.module_object_id)
  WHEN matched THEN
    UPDATE
       set last_updated_date = sysdate,
           bmav.field_1      = t2.eom,
           bmav.field_2      = t2.name_zh_n bmav.field_3 = t2.name_en_us,
           bmav.field_4      = t2.description_zh_cn,
           bmav.field_5      = t2.description_en_us,
           bmav.field_6      = t2.salestype,
           bmav.field_7      = t2.offeringld
  WHEN NOT matched THEN
    INSERT
      (object_id, class_id, module_object_id, created_date,last_updated_date,field_1,field_2,field_3,field_4,field_5,field_6,field_7)
    VALUES(seq_object_list_id.nextval, seq_object_list_id.nextval, t2.src_object_id, sysdate, sysdate, t2.eom, t2.name_zh_cn, t2.name_en_us,  t2.description_zh_cn,  t2.description_en_us, t2.salestype, t2.offeringld);
END;




SELECT program_name,
       code,
       name,
       greatest(nvl(to_number(Substr(program_name, 11)) .0),
                nvl(to_number(substr(code, 11)), 0),
                nvl(to_number(substr(name, 11)), o)) max
  FROM (SELECT (SELECT program_name
                  FROM (SELECT ma.program_name
                          FROM (SELECT mc1.object_id
                                FROM m_classification mc1
                                START WITH mc1.object_id = 1
                                CONNECT BY mc1.class_id = PRIOR mc1.father_class_id
                                UNION
                                SELECT mc2.object_idfrom m_classification mc2
                                START WITH mc2.object_id = 1
                                CONNECT BY PRIOR mc2.class_id = mc2.father_class_id) mc
                         inner join m_object_relation mor ON mor.src_object_id = mc.object_id
                         inner join m_attribute ma ON ma.object_id = mor.dst_object_id
                         WHERE regexp_like(ma.program_name, 'attribute_1')
                         ORDER BY to_number(substr(ma.program_name, 11)) DESC)
                 WHERE ROWNUM <= 1) AS program_name,
               (SELECT code
                  FROM (SELECT ma.code
                          FROM (SELECT mc1.object_id
                                FROM m_classification mc1
                                START WITH mc1.oject_id = 1
                                CONNECT BY mc1.class_id = PRIOR mc1.father_class_id
                                UNION
                                SELECT mc2.object_id
                                FROM m_classification mc2
                                START WITH mc2.object_id = 1
                                CONNECT BY PRIOR mc2.class_id = mc2.father_class_id) mc
                         inner join m_object relation mor ON mor.src_object_id = mc.object_id
                         inner join m_attribute ma ON ma.object_id = mor.dst_object_id
                         WHERE regexp_like(ma.code, 'attribute_1')
                         ORDER BY to_number(substr(ma.code, 11)) DESC)
                 WHERE ROWNUM <= 1) AS code,
               (SELECT name
                  FROM (SELECT ma.name
                          FROM (SELECT mc1.object_id
                                FROM m_classification mc1
                                START WITH mc1.object_id = 1
                                CONNECT BY mc1.class_id = PRIOR mc1.father_class_id
                                UNION
                                SELECT mc2.object_id
                                FROM m_classification mc2
                                START WITH nc2.object_id = 1
                                CONNECT BY PRIOR mc2.class_id = c2.father_class_id) mc
                         inner join m_object_relation mor ON mor.src_object_id = mc.object_id
                         inner join m_attribite ma ON ma.object_id = mor.dst_object_id
                         WHERE regexp_like(ma.name, 'attribute_1')
                         ORDER BY to_number(substr(ma.name, 11)) des)
                 WHERE ROWNUM <= 1) AS name
          FROM dual);





