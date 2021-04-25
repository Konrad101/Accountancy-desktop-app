package controllers.companyData.operations.showingDetails;

import models.TableData;

public interface DetailsOperation {
    void setTableData(TableData data);

    void loadDataToComponents();
}
