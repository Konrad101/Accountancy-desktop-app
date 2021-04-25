package controllers.companyData.operations.edit;

import models.TableData;

public interface EditOperation {
    void setID(int id);

    void setTableData(TableData data);

    void loadDataToComponents();
}
