for i in {1..90};
do
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -d master
    if [ $? -eq 0 ]
    then
        echo "SQL server started"
        break
    else
        echo "SQL server not ready yet..."
        sleep 1
    fi
done
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -d master -i init-schema.sql
echo "init-schema.sql completed"