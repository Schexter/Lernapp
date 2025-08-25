"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.useExamStore = void 0;
var zustand_1 = require("zustand");
exports.useExamStore = (0, zustand_1.create)(function (set) { return ({
    examQuestions: [],
    currentQuestionIndex: 0,
    userAnswers: {},
    timeRemaining: 0,
    examStartTime: null,
    examEndTime: null,
    isExamActive: false,
    startExam: function (questions) {
        return set({
            examQuestions: questions,
            isExamActive: true,
            examStartTime: new Date(),
            currentQuestionIndex: 0,
            userAnswers: {},
            timeRemaining: questions.length * 90 // 90 seconds per question
        });
    },
    submitAnswer: function (questionId, answer) {
        return set(function (state) {
            var _a;
            return ({
                userAnswers: __assign(__assign({}, state.userAnswers), (_a = {}, _a[questionId] = answer, _a))
            });
        });
    },
    nextQuestion: function () {
        return set(function (state) { return ({
            currentQuestionIndex: Math.min(state.currentQuestionIndex + 1, state.examQuestions.length - 1)
        }); });
    },
    previousQuestion: function () {
        return set(function (state) { return ({
            currentQuestionIndex: Math.max(state.currentQuestionIndex - 1, 0)
        }); });
    },
    setTimeRemaining: function (time) {
        return set({ timeRemaining: time });
    },
    endExam: function () {
        return set({
            isExamActive: false,
            examEndTime: new Date()
        });
    },
    reset: function () {
        return set({
            examQuestions: [],
            currentQuestionIndex: 0,
            userAnswers: {},
            timeRemaining: 0,
            examStartTime: null,
            examEndTime: null,
            isExamActive: false
        });
    }
}); });
