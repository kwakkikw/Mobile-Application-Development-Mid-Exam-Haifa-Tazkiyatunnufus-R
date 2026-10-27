package com.example.mad_midexam_it2_001202500138_haifatazkiyatunnufusr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    List<StudentModel> student;
    Context context;
    DBHelper dbHelper;

    public StudentAdapter(List<StudentModel> student, Context context) {
        this.student = student;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.student_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final StudentModel studentModel = student.get(position);
        holder.textViewID.setText("No. " + studentModel.getId());
        holder.editTextName.setText(studentModel.getName());
        holder.editTextStudentId.setText(studentModel.getStudentId());
        holder.checkBoxEnrolled.setChecked(studentModel.isEnrolled());

        holder.btnEdit.setOnClickListener(view -> {
            String strName = holder.editTextName.getText().toString().trim();
            String strStudentId = holder.editTextStudentId.getText().toString().trim();
            boolean enrolled = holder.checkBoxEnrolled.isChecked();

            if (strName.isEmpty() || strStudentId.isEmpty()) {
                Toast.makeText(context, "Enter data", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.updateStudent(
                    new StudentModel(studentModel.getId(), strName, strStudentId, enrolled));
            if (success) {
                studentModel.setName(strName);
                studentModel.setStudentId(strStudentId);
                studentModel.setEnrolled(enrolled);
                Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Student ID already exists!", Toast.LENGTH_SHORT).show();
                holder.editTextStudentId.setText(studentModel.getStudentId());
            }
        });

        holder.btnDelete.setOnClickListener(view -> {
            int pos = holder.getAbsoluteAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            dbHelper.deleteStudent(studentModel.getId());
            student.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, student.size());
            Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
            if (student.isEmpty()) {
                Toast.makeText(context, "No Student Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return student.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewID;
        EditText editTextName, editTextStudentId;
        CheckBox checkBoxEnrolled;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewID = itemView.findViewById(R.id.textViewID);
            editTextName = itemView.findViewById(R.id.editTextName);
            editTextStudentId = itemView.findViewById(R.id.editTextStudentId);
            checkBoxEnrolled = itemView.findViewById(R.id.checkBoxEnrolled);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
