package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 地址簿管理
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 保存地址簿
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        log.info("addressBook={}", addressBook);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @Transactional
    public R<String> update(@RequestBody AddressBook addressBook) {
        log.info("addressBook={}", addressBook);
//        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(addressBook != null, AddressBook::getId, addressBook.getId());
//        addressBookService.remove(queryWrapper);
//        addressBookService.save(addressBook);
        addressBookService.updateById(addressBook);
        return R.success("地址修改成功！");

    }

    /**
     * 根据id删除地址
     * @param ids
     * @return
     */
    @DeleteMapping()
    @Transactional
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids={}", ids);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AddressBook::getId, ids);
        int count = addressBookService.count(queryWrapper);
        if (count > 0) {
            addressBookService.removeByIds(ids);
            LambdaQueryWrapper<AddressBook> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(AddressBook::getUserId, BaseContext.getCurrentId()).eq(AddressBook::getIsDefault,1);
            int count1 = addressBookService.count(queryWrapper2);
            if (count1 == 0) {
                LambdaQueryWrapper<AddressBook> queryWrapper3 = new LambdaQueryWrapper<>();
                queryWrapper3.eq(AddressBook::getUserId, BaseContext.getCurrentId());
                List<AddressBook> list = addressBookService.list(queryWrapper3);
                AddressBook addressBook = list.get(0);
                addressBook.setIsDefault(1);
                addressBookService.updateById(addressBook);
            }
            return R.success("删除成功！");
        }
        return R.error("不存该数据！");
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("addressBook={}",addressBook);
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设置默认地址成功！");
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefalutAddress() {
        log.info("查询默认地址={}");
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId).eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> query(@PathVariable Long id) {
        log.info("id={}",id);
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null){
            return R.success(addressBook);
        }
        return R.error("找不到该对象");
    }

    /**
     * 查询地址列表
     * @param addressBook
     * @return
     */
    @GetMapping("list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId, addressBook.getUserId()).orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        log.info("addressBook={}",list);
        return R.success(list);
    }
}
