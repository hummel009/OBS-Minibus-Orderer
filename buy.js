// Устанавливаем время запуска на завтра
let scheduledTime = new Date();
scheduledTime.setDate(scheduledTime.getDate() + 1);
scheduledTime.setHours(3);
scheduledTime.setMinutes(30);
scheduledTime.setSeconds(0);

// Вычисляем интервал времени между текущим временем и заданным временем запуска
let timeUntilScheduledTime = scheduledTime.getTime() - Date.now();

// Вычисляем количество миллисекунд в часе и минуте
const msPerMinute = 60 * 1000;
const msPerHour = msPerMinute * 60;

// Вычисляем количество часов и минут до запуска
const hoursUntilScheduledTime = Math.floor(timeUntilScheduledTime / msPerHour);
const minutesUntilScheduledTime = Math.floor((timeUntilScheduledTime % msPerHour) / msPerMinute);

// Выводим результаты
console.log(`До запуска осталось ${hoursUntilScheduledTime} ч. ${minutesUntilScheduledTime} мин.`);

// Запускаем выполнение кода через указанный интервал времени
setTimeout(function() {
    let blocks = [
		block1,
		block2,
		block3,
		block4,
		block5,
		block6,
		//block65,
		block7,
		block8,
		block9,
		block10,
		block11,
		block12,
		block13,
		block14
	];

    let counter = 0;

    // Запускаем выполнение функций с интервалом в 3 секунды
    let intervalID = setInterval(function() {
        blocks[counter]();
        counter++;

        // Если выполнены все функции, останавливаем интервал
        if (counter === blocks.length) {
            clearInterval(intervalID);
        }
    }, 10000);
}, timeUntilScheduledTime);

//отмотать к форме
function block1() {
	const elems = document.querySelectorAll('.sc-bdVaJa');
	elems[0].click();
}

//открыть список "откуда"
function block2() {
	const elems = document.querySelectorAll('.sc-kgoBCf');
	elems[0].click();
}

//выбрать "откуда"
function block3() {
	const elems = document.querySelectorAll('.sc-ckVGcZ');
	for (let i = 0; i < elems.length; i++) {
		if (elems[i].textContent === 'Логойск (Логойский р-н., Минская обл.)') {
			elems[i].click();
			break;
		}
	}
}

//открыть список "куда"
function block4() {
	const elems = document.querySelectorAll('.sc-kgoBCf');
	elems[1].click();
}

//выбрать "куда"
function block5() {
	const elems = document.querySelectorAll('.sc-ckVGcZ');
	for (let i = 0; i < elems.length; i++) {
		if (elems[i].textContent === 'Минск (Минский р-н., Минская обл.)') {
			elems[i].click();
			break;
		}
	}
}

//открыть выбор времени
function block6() {
	document.querySelector('.sc-dnqmqq').click();
}

//открыть выбор времени
function block65() {
	document.querySelector('.DayPicker-NavButton--next').click();
}

//выбрать время
function block7() {
	const elems = document.querySelectorAll('.DayPicker-Day');
	for (let i = 0; i < elems.length; i++) {
		if (elems[i].textContent === '2') {
			elems[i].click();
			break;
		}
	}
}

//нажать поиск
function block8() {
	const elems = document.querySelectorAll('.sc-bdVaJa');
	elems[1].click();
}

//выбрать билет
function block9() {
	const elems = document.querySelectorAll('.sc-bdVaJa');
	elems[7].click();
}

//открыть список "остановка отправления"
function block10() {
	const elems = document.querySelectorAll('.sc-kgoBCf');
	elems[0].click();
}

//выбрать остановку отправления
function block11() {
	const elems = document.querySelectorAll('.sc-ckVGcZ');
	for (let i = 0; i < elems.length; i++) {
		if (elems[i].textContent.includes('РДК (Логойск)')) {
			elems[i].click();
			break;
		}
	}
}

//открыть список "остановка прибытия"
function block12() {
	const elems = document.querySelectorAll('.sc-kgoBCf');
	elems[1].click();
}

//выбрать остановку прибытия
function block13() {
	const elems = document.querySelectorAll('.sc-ckVGcZ');
	for (let i = 0; i < elems.length; i++) {
		if (elems[i].textContent.includes('Восток (Минск)')) {
			elems[i].click();
			break;
		}
	}
}

function block14() {
	const elems = document.querySelectorAll('.sc-bdVaJa');
	elems[0].click();
}