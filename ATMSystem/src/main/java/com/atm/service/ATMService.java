package com.atm.service;

import com.atm.dto.ApiResponse;
import com.atm.model.ATMStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atm.model.ATM;
import com.atm.repository.ATMRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ATMService {
    private final ATMRepository atmRepository;
    int[] denominations = {500000, 200000, 100000, 50000};

    @Autowired
    public ATMService(ATMRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

//    // Kiểm tra trạng thái của máy ATM
//    public String checkATMStatus() {
//        return atmRepository.findATMStatus(); // Giả sử hàm này trả về trạng thái của máy ATM
//    }

//    // Cập nhật trạng thái máy ATM
//    public void updateATMStatus(String new_status) {
//        ATM atm = atmRepository.findATM();
//        atm.setStatus(new_status); // Cập nhật trạng thái mới
//        atmRepository.save(atm); // Lưu thay đổi vào cơ sở dữ liệu
//    }
//
//    // Kiểm tra xem máy ATM có đủ tiền mặt hay không
//    public boolean checkCashAvailability() {
//        ATM atm = atmRepository.findATM();
//        return atm.getTotalCash() > 0; // Kiểm tra tổng tiền mặt trong máy ATM
//    }

    public Map<Integer,Integer> getCashInATM(){
        Map<Integer,Integer> cashInATM = new HashMap<>();

        //Lấy thông tin lượng tiền từ ATM
        Optional<ATM> ATMInfo = atmRepository.findById(1L);
        if (ATMInfo.isPresent()) {
            ATM currentATM = ATMInfo.get();

            cashInATM.put(denominations[0], currentATM.getCash500());
            cashInATM.put(denominations[1], currentATM.getCash200());
            cashInATM.put(denominations[2], currentATM.getCash100());
            cashInATM.put(denominations[3], currentATM.getCash50());

        }

        return cashInATM;
    }

    public void updateATMCash(Map<Integer,Integer> cashInATM){
        ATM atm = atmRepository.findById(1L).get();
        atm.setCash500(cashInATM.get(denominations[0]));
        atm.setCash200(cashInATM.get(denominations[1]));
        atm.setCash100(cashInATM.get(denominations[2]));
        atm.setCash50(cashInATM.get(denominations[3]));
        atmRepository.save(atm);
    }

    public void updateATMStatus(ATMStatus newStatus){
        ATM atm = atmRepository.findById(1L).get();
        atm.setStatus(newStatus);
        atmRepository.save(atm);
    }

    public ATMStatus getATMStatus(){
        ATM atm = atmRepository.findById(1L).get();
        return atm.getStatus();
    }

    public double withdraw(double amount) {

        //Lấy số lượng tiền mặt trong ATM
        Map<Integer,Integer> cashInATM = getCashInATM();


        Map<Integer, Integer> result = new HashMap<>();
        int cashAmount = (int) amount;
        for (int denomination : denominations) {
            if (cashInATM.getOrDefault((int) denomination, 0) > 0) {
                int maxBills = cashAmount / denomination;
                int actualBills = Math.min(maxBills, cashInATM.get(denomination));

                if (actualBills > 0) {
                    result.put(denomination, actualBills);
                    cashInATM.put(denomination, cashInATM.get(denomination) - actualBills);
                    cashAmount -= actualBills * denomination;
                }
            }
        }

        if (cashAmount > 0) {
            throw new IllegalArgumentException("Không thể rút số tiền này với các mệnh giá hiện tại!");
        }


        //Cập nhật số lượng rút vào ATM
        ATM ATMInfo = atmRepository.findATM();
        if (ATMInfo != null) {
            ATMInfo.setCash500(cashInATM.get(denominations[0]));
            ATMInfo.setCash200(cashInATM.get(denominations[1]));
            ATMInfo.setCash100(cashInATM.get(denominations[2]));
            ATMInfo.setCash50(cashInATM.get(denominations[3]));

            double totalAmount = ATMInfo.getCash500()* denominations[0]
                    + ATMInfo.getCash200()* denominations[1]
                    + ATMInfo.getCash100()*denominations[2]
                    + ATMInfo.getCash50() * denominations[3];

            ATMInfo.setTotalAmount(totalAmount);
            atmRepository.save(ATMInfo);
        }
        int roundedAmount = (int) amount;
        return (double) roundedAmount;
    }

        // Nạp tiền vào máy ATM
    public void refillCash(int cash_500, int cash_200, int cash_100, int cash_50) {
        ATM atm = atmRepository.findATM();
        atm.setCash500(atm.getCash500() + cash_500);
        atm.setCash200(atm.getCash200() + cash_200);
        atm.setCash100(atm.getCash100() + cash_100);
        atm.setCash50(atm.getCash50() + cash_50);
        atmRepository.save(atm); // Lưu thông tin mới vào cơ sở dữ liệu
    }
}