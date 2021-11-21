package com.dykoone.rosehouseplugin.system;

import com.dykoone.bank.BankPlugin;
import com.dykoone.bank.PlayerConfig;
import com.dykoone.bank.data.PlayerInfoApi;
import com.dykoone.bank.data.PlayerInfoDataObject;
import com.dykoone.guikitplugin.util.GuiListKit;
import com.dykoone.rosehouseplugin.RoseHousePlugin;
import com.dykoone.rosehouseplugin.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameRoom extends GuiListKit {
    /*
     * joinCoolTime : 플레이어 방입장 준비시간 (해당 쿨타임은 플레이어가 들어왔을경우 초기화된다)
     *  ㄴ 방입장 쿨타임은 최대 3번만 초기화된다. 3번이 초과될경우 플레이어가 입장해도 초기화 되지 않는다.
     *
     */
    private final int JOIN_COOL = 5;
    private final int GAME_COOL = 20;
    private final int JOIN_MAX_COOL_RESET = 3;
    private final int RESULT_TIME = 5;

    private int taskId = -1;
    private List<Player> players = new ArrayList<>(9);
    private final int slot = 36; //플레이어 입장 첫번째 자리
    private int joinCoolTime; //플레이어 방입장 준비시간 (해당 쿨타임은 플레이어가 들어왔을경우 초기화된다)
    private int gameCoolTime; //플레이어 게임 준비시간
    private int joinMaxCoolTimeReset; //플레이어 입장 쿨타임 최대리셋 수
    private int gameResultTime = RESULT_TIME; //게임이 끝난후 결과 쿨타임
    private int minGameRunTime, maxGameRunTime; //게임 최소-최대 플레이타임
    private Inventory gameRoomInventory = null; //플레이어 최초방생성자 인벤토리
    private int gameGold = 0;
    private int multiply; //해당 게임 배수
    private boolean isClickEvent;
    private boolean isJoin = false;
    private boolean isGameStart = false;

    //쿨타임 리셋
    private void resetCoolTimeAll() {
        joinCoolTime = JOIN_COOL;
        gameCoolTime = GAME_COOL;
        joinMaxCoolTimeReset = JOIN_MAX_COOL_RESET;
        isClickEvent = false;
        selectNumber.clear();
    }

    public GameRoom() {
        resetCoolTimeAll();
        minGameRunTime = JOIN_COOL + GAME_COOL + RESULT_TIME;
        maxGameRunTime = (JOIN_COOL * JOIN_MAX_COOL_RESET) + GAME_COOL + RESULT_TIME;
    }

    //인벤토리 add (최초 방생성시에만 적용한다)
    public GameRoom setGameRoomInventory(Inventory inventory) {
        gameRoomInventory = inventory;
        return this;
    }

    public Inventory getGameRoomInventory() {
        return gameRoomInventory;
    }

    //게임 시작
    public void start() {
        //게임 시작시 :
        // 1.해당 게임 배수 결정
        // 2.플레이어들 배팅 시간 (최대배팅 시간 20초, 모든플레이어가 배팅 완료시 최대배팅시간 전에 게임이 시작된다)
        //   ㄴ 배팅시간은 만약 자리고르기 게임일경우 자리고르는 시간 포함
        // 3.결과 보여주기 5초 ( 배팅 금액 정산 )

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "start");

        if (taskId == -1) {
            multiply = goldMultiply();
            selectSlot();
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(RoseHousePlugin.getInstance(), () -> {
                if (!isGameStart) {
                    if (getPlayerSize() == 9) {
                        isGameStart = true;
                        isClickEvent = true; //인원이 꽉차고 클릭 이벤트가 열린다.
                        setClickItemType(true);
                    }

                    if (joinCoolTime > 0) {
                        joinCoolTime--;
                    }

                    if (isJoin && joinMaxCoolTimeReset > 0) {
                        joinCoolTime = JOIN_COOL;
                        joinMaxCoolTimeReset--;
                    }
                    if (joinCoolTime <= 0) {
                        isGameStart = true;
                        isClickEvent = true; //게임 대기시간이 끝나고 클릭 이벤트가 열린다.
                        setClickItemType(true);
                    }
                } else {
                    //2.플레이어들 배팅 시간
                    if (gameCoolTime > 0) {
                        gameCoolTime--;
                    } else {
                        //배팅 또는 선택의 시간이 끝난후
                        isGameStart = false;
                        isClickEvent = false;
                        setClickItemType(false);
                        viewRose();
                        resultGameGold();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                finish(); //게임종료를 알린다.
                                Bukkit.getScheduler().cancelTask(taskId);
                            }
                        }.runTaskLater(RoseHousePlugin.getInstance(), 80L);
                    }
                }
            }, 10L, 20L);
            taskId = task.getTaskId();
        }
    }

    //게임 종료
    public void finish() {
        resetRoom();
        resetGold();
        resetCoolTimeAll();
        taskId = -1;
    }

    //게임진행 아이템 ( slot = 18 연두색 : 클릭 가능, 빨간색 : 클릭 불가 )
    private void setClickItemType(boolean isClickEvent) {
        if (isClickEvent) {
            gameRoomInventory.setItem(18, new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
        } else {
            gameRoomInventory.setItem(18, new ItemStack(Material.RED_STAINED_GLASS_PANE));
        }
    }

    //게임 배수 설정
    private int goldMultiply() {
        return ServerUtil.randomNumber(3);
    }

    //다음 게임을 위한 보더 리셋 ( 잔디영역만 변경 )
    private void resetRoom() {
        Inventory inventory = gameRoomInventory;
        for (int i = 0; i < 8; i++) {
            removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 1, inventory);
            removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 10, inventory);
            removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 19, inventory);
            removeFlagStack(" ", Material.GRASS_BLOCK, 1, null, i + 28, inventory);
        }
    }

    //플레이어 배팅 골드 초기화
    private void resetGold() {
        if (gameRoomInventory != null) {
            Stream<ItemStack> inventory = Arrays.stream(gameRoomInventory.getContents());
            List<ItemStack> goldItem = inventory.skip(44).limit(9)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            goldItem.forEach(itemStack -> {
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "0");
                itemStack.setItemMeta(itemMeta);
            });
        }
    }

    //플레이어 사이즈(null 이아닌경우)
    public int getPlayerSize() {
        return (int) players.stream().filter(Objects::nonNull).count();
    }

    //플레이어 idx
    public int getPlayerIdx(Player player) {
        return players.indexOf(player);
    }

    public void onJoin(Player player) {
        isJoin = true;
        int number;
        int idx = players.indexOf(null);
        if (idx == -1) {
            players.add(player);
            number = players.size() - 1;
        } else {
            number = idx;
        }
        gameRoomInventory.setItem(number + slot, ServerUtil.getPlayerSkull(player));
        player.openInventory(gameRoomInventory);
    }

    public void onQuit(Player player) {
        int number = players.indexOf(player);
        players.set(number, null);
        int itemSlot = number + slot;
        gameRoomInventory.setItem(itemSlot, new ItemStack(Material.ARMOR_STAND));
        ItemStack itemStack = gameRoomInventory.getItem(itemSlot + 9);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "0");
        itemStack.setItemMeta(itemMeta);
        Objects.requireNonNull(player.openInventory(gameRoomInventory)).close();
    }

    //해당 꽃 자리 정하기
    private Set<Integer> selectNumber = new HashSet<>(5);

    private void selectSlot() {
        do {
            selectNumber.add(new Random().nextInt(32));
        } while (selectNumber.size() < 5);
    }

    //선택된 번호에 꽃이 인벤토리에 변경
    private void viewRose() {
        selectNumber.forEach(integer -> {
            int slot = integer;
            if (slot >= 0 && slot <= 7) {
                slot = integer + 1;
            } else if (slot >= 8 && slot <= 15) {
                slot = integer + 2;
            } else if (slot >= 16 && slot <= 23) {
                slot = integer + 3;
            } else if (slot >= 24 && slot <= 31) {
                slot = integer + 4;
            } else {
                slot = -1;
            }
            gameRoomInventory.setItem(slot, new ItemStack(Material.ROSE_BUSH));
        });
    }

    //플레이어에게 배당금 지급
    private void resultGameGold() {
        List<Player> playerList = new ArrayList<>(players);
        playerList.forEach(player -> {
            int idx = playerList.indexOf(player);
            int playerSkullSlot = idx + 36;
            ItemStack skull = gameRoomInventory.getItem(playerSkullSlot);
            int skullAmount = skull.getAmount();
            if (selectNumber.contains(skullAmount)) {
                int goldSlot = idx + 45;
                ItemStack itemStack = gameRoomInventory.getItem(goldSlot);
                ItemMeta itemMeta = itemStack.getItemMeta();
                String goldString = ChatColor.stripColor(itemMeta.getDisplayName());
                int gold = Integer.parseInt(goldString);
                PlayerInfoApi api = PlayerConfig.getPlayerInfoDataImpl();
                PlayerInfoDataObject object = api.getPlayerInfoData(player.getUniqueId());
                int playerGold = Integer.parseInt(object.getGold());
                String resultGold = String.valueOf(playerGold + gold);
                object.setGold(resultGold);
            }
        });
    }

    //해당 플레이어가 닫기버튼이 아닌 비정상적으로 닫았을경우 인벤토리가 다시 열린다.
    public void reOpenInventory(Player player) {
        player.openInventory(gameRoomInventory);
    }

    public boolean isClickEvent() {
        return isClickEvent;
    }
}
