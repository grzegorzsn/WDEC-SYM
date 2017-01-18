package com.wdec;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * Created by Grzegorz on 2017-01-17.
 */
public class GUI {
    private JPanel panelMain;
    private JFormattedTextField availableMoneyTxt;
    private JLabel riskLbl;
    private JLabel availableMoneyLbl;
    private JFormattedTextField desiredRiskTxt;
    private JButton searchSolutionBtn;
    private JLabel volumeLbl;
    private JRadioButton includeRiskRbYes;
    private JRadioButton includeRiskRbNo;
    private JLabel includeRiskLbl;
    private JLabel qualityLbl;
    private JLabel commercialCostsLbl;
    private JLabel priceLbl;
    private JFormattedTextField volumeTxt;
    private JFormattedTextField qualityTxt;
    private JFormattedTextField commercialCostsTxt;
    private JFormattedTextField priceTxt;
    private JLabel riskPredictedLbl;
    private JFormattedTextField riskPredictedTxt;
    private JLabel predictedProfitLbl;
    private JFormattedTextField predictedProfitTxt;
    private static JFrame frame;


    private String databasePath = "db.csv";
    private String costsDataPath = "samplecosts.csv";
    private DecisionDatabase decisionDatabase;

    public GUI()
    {
        try {
            decisionDatabase = new DecisionDatabase(databasePath, costsDataPath);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, "Nie udało się wczytać bazy danych!");
            System.exit(1);
        }
        includeRiskRbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desiredRiskTxt.setEnabled(includeRiskRbYes.isSelected());
                desiredRiskTxt.setText("");
            }
        });

        includeRiskRbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desiredRiskTxt.setEnabled(includeRiskRbYes.isSelected());
            }
        });
        searchSolutionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processDeciding();
            }
        });


    }

    public static void main(String[] args)
    {
        frame = new JFrame("Wspomagacz");
        frame.setContentPane(new GUI().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void processDeciding()
    {
        BigDecimal availableMoney;
        BigDecimal desiredRisk = null;

        try{
            availableMoney = new BigDecimal(availableMoneyTxt.getText());
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Dostępne środki muszą być reprezentowane przez liczbę!");
            return;
        }

        if(!desiredRiskTxt.getText().equals("")) {
            try {
                desiredRisk = new BigDecimal(desiredRiskTxt.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Oczekiwane ryzyko musi być reprezentowane przez liczbę od 0 do 1!");
                return;
            }
            if (desiredRisk.compareTo(BigDecimal.ZERO) == -1 || desiredRisk.compareTo(BigDecimal.ONE) == 1) {
                JOptionPane.showMessageDialog(null, "Oczekiwane ryzyko musi być reprezentowane przez liczbę od 0 do 1!");
                return;
            }
        }

        Decision decison = DecisionMaker.decide(decisionDatabase, availableMoney, desiredRisk);

        if( decison == null)
        {
            JOptionPane.showMessageDialog(null, "Nie udało się znaleźć zadowalającego rozwiązania.");
            return;
        }

        volumeTxt.setText(decison.getVolume().toString());
        qualityTxt.setText(decison.getQuality().toString());
        priceTxt.setText(decison.getPrice().toString());
        commercialCostsTxt.setText(decison.getCommCosts().toString());
        riskPredictedTxt.setText(decison.getRisk().toString());
        predictedProfitTxt.setText(decison.getProfit().toString());
    }

}
