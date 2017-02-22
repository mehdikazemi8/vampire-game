package ir.ugstudio.vampire.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.listeners.OnCompleteListener;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.models.StoreItemVirtual;
import ir.ugstudio.vampire.utils.StoreDescriptionSelector;
import ir.ugstudio.vampire.utils.StoreIconSelector;
import ir.ugstudio.vampire.views.custom.CustomButton;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class IntroduceVirtualItemDialog extends Dialog {

    private StoreItemVirtual item;
    private OnCompleteListener onCompleteListener;

    private ImageView picture;
    private CustomTextView description;
    private CustomButton continuePurchase;
    private CustomButton refusePurchase;

    public IntroduceVirtualItemDialog(Context context, StoreItemVirtual item, OnCompleteListener onCompleteListener) {
        super(context);
        this.item = item;
        this.onCompleteListener = onCompleteListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_introduce_virtual_item, null);
//        forceWrapContent(view);

        setContentView(R.layout.dialog_introduce_virtual_item);
//        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        getWindow().setLayout((int) (metrics.widthPixels * 0.90), 0);

        find();
        configure();
    }

    protected void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                current.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }

    private void find() {
        picture = (ImageView) findViewById(R.id.virtual_picture);
        description = (CustomTextView) findViewById(R.id.virtual_description);
        refusePurchase = (CustomButton) findViewById(R.id.refuse_purchase);
        continuePurchase = (CustomButton) findViewById(R.id.continue_purchase);
    }

    private void configure() {
        refusePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompleteListener.onComplete(0);
                dismiss();
            }
        });

        continuePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompleteListener.onComplete(1);
                dismiss();
            }
        });

        Picasso.with(getContext()).load(StoreIconSelector.getVirtualStoreItem(getContext(),
                item.getImageType(),
                CacheHandler.getUser().getRole())).into(picture);

        description.setText(StoreDescriptionSelector.getDescription(getContext(), item.getItemId()));
    }
}
