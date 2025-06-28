package com.school.schoolmanagement.Adapters;

import com.school.schoolmanagement.Admin.Model.AccountGet;

public interface ChartItemClickListener {
    void onEditClick(AccountGet.Datum item, int position);
    void onDeleteClick(AccountGet.Datum item, int position);
}
