package com.develop.autorus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;

/**
 * Created by Alex on 06.10.2015.
 */
public class ReferenceFragment extends android.support.v4.app.Fragment {
    View savedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.init(getActivity(), 2, 0, null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String themeName = pref.getString("theme", "1");
        if (themeName.equals("1")) {
            getActivity().setTheme(R.style.AppTheme);
        }
        else if (themeName.equals("2")) {
            getActivity().setTheme(R.style.AppTheme2);
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedView = inflater.inflate(R.layout.fragment_reference, container, false);
        TextView reference = (TextView) savedView.findViewById(R.id.text_view_reference);
        reference.setText("\t\tПриложение создано для того, чтобы облегчить утомительный процесс поиска Вашего будущего автомобиля.\n\n" +
                "\t\tДля этого в нем имеются мониторы, которые будут вести поиск по указанным параметрам на ведущих автомобильных порталах России.\n\n" +
                "\t\tМониторы, которые Вы активировали, периодически обновляют информацию по подходящим к ним объявлениям и уведомляют Вас о появлении новых авто. " +
                "Величина периода зависит от количества мониторов. " +
                "Неактивные мониторы выполняют функции сохранения настроек фильтрации.\n\n" +
                "\t\tВы используете первую версию приложения, которое активно развивается, поэтому Ваши отзывы и предложения помогут нам сделать его лучше и эффективнее. Удачных покупок!\n");
        /*try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            version.setText("Номер версии: "+ versionName);
        }
        catch (PackageManager.NameNotFoundException e) {}
        version.setText(version.getText() +"\nEmail: room530a@gmail.com" );*/

        final com.rey.material.widget.Button button =
                (com.rey.material.widget.Button) savedView.findViewById(R.id.button_bt_flat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Изменить на другой адрес.
                String packageName =savedView.getContext().getPackageName(); ;//"com.develop.searchmycarandroid";
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + packageName));
                    //id=" +getPackageName(), можно так, только тогда сейчас ничего работать не будет.
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                }
            }
        });

        return savedView;
    }

}

