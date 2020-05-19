package ch.epfl.polycrowd.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import ch.epfl.polycrowd.R;

//https://developer.android.com/guide/topics/ui/dialogs.html

public class CreateGroupDialogFragment extends DialogFragment {

    public interface CreateGroupDialogListener{
        public void onOKCreateGroupClick(DialogFragment dialog, String groupName, String eventId) ;
    }

    CreateGroupDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (CreateGroupDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement CreateGroupDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_create_group, null) ;
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view).setPositiveButton(R.string.group_submit, (dialog, which) -> {
            String groupName = ((EditText)view.findViewById(R.id.groupNameEditText)).getText().toString() ;
            String eventId = ((EditText)view.findViewById(R.id.eventIdEditText)).getText().toString() ;
            listener.onOKCreateGroupClick(this, groupName, eventId);
        }) ;

        return builder.create();
    }
}
