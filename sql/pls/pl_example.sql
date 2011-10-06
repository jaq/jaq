declare
 fecha1 varchar2(250);
begin
 select to_char(sysdate,'DD-MM-YYYY hh24:mi:ss') into fecha1 from dual;
 dbms_output.put_line('Fecha:' || fecha1);
end;